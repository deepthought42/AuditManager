# Audit Manager

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/e2376d355755402aaa5bf7c533750851)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=deepthought42/WebTestVisualizer&amp;utm_campaign=Badge_Grade)

## Overview

The **Audit Manager** is a Spring Boot microservice that orchestrates web page audits as part of the Look-see platform. It serves as the central coordinator for managing and tracking comprehensive website audits, including visual design, information architecture, and content quality assessments.

## What It Does

The Audit Manager is responsible for:

- **Audit Orchestration**: Receives page build notifications and coordinates the execution of various audit types
- **Audit Tracking**: Maintains audit records and tracks the status of individual page audits
- **Message Routing**: Publishes audit messages to appropriate microservices via Google Cloud Pub/Sub
- **Duplicate Prevention**: Ensures pages are not audited multiple times
- **Audit Type Management**: Configures and manages different types of audits based on requirements

## Supported Audit Types

The system supports the following audit categories:

### Visual Design Audits
- **Text Background Contrast**: Evaluates text readability against background colors
- **Non-Text Background Contrast**: Assesses contrast for UI elements and graphics

### Information Architecture Audits
- **Links**: Analyzes link structure and navigation
- **Titles**: Evaluates page titles and heading hierarchy
- **Encrypted**: Checks for proper encryption and security
- **Metadata**: Reviews meta tags and SEO elements

### Content Audits
- **Alt Text**: Validates image accessibility with proper alt text
- **Reading Complexity**: Analyzes content readability and complexity
- **Paragraphing**: Evaluates content structure and paragraph organization
- **Image Copyright**: Checks for proper image licensing and attribution
- **Image Policy**: Ensures images comply with organizational policies

## Architecture

### Technology Stack
- **Java 21**: Core programming language
- **Spring Boot 2.6.13**: Application framework
- **Neo4j**: Graph database for audit record storage
- **Google Cloud Pub/Sub**: Message queuing and event streaming
- **Selenium**: Web automation for page analysis
- **Google Cloud Vision API**: Image analysis capabilities
- **Resilience4j**: Circuit breaker and retry mechanisms

### Key Components

#### 1. AuditController
The main REST controller that:
- Receives Base64-encoded messages from Pub/Sub
- Processes `PageBuiltMessage` notifications
- Creates audit records and publishes audit messages
- Prevents duplicate audits

#### 2. AuditRecordService
Manages audit record lifecycle:
- Creates and updates audit records
- Tracks audit status and progress
- Links page audits to domain audits
- Prevents duplicate page audits

#### 3. PageStateService
Handles page state information:
- Validates if pages are "landable" (accessible)
- Retrieves page metadata and URLs
- Manages page state persistence

#### 4. PubSubPageAuditPublisherImpl
Publishes audit messages to:
- Page audit topic for individual page processing
- Audit update topic for progress notifications

### Data Models

#### AuditRecord (Base)
- Abstract base class for all audit records
- Contains common audit metadata

#### DomainAuditRecord
- Represents a domain-wide audit
- Contains audit configuration and labels
- Links to multiple page audits

#### PageAuditRecord
- Represents a single page audit
- Contains page state and audit configuration
- Tracks execution status

#### PageState
- Contains page metadata (URL, accessibility, etc.)
- Used to determine if pages should be audited

## How It Works

### 1. Message Reception
The service receives Base64-encoded messages via HTTP POST to the root endpoint (`/`). Messages contain:
- Account ID
- Page ID
- Audit Record ID
- Page metadata

### 2. Message Processing
1. **Decode**: Base64 decode the incoming message
2. **Parse**: Convert JSON to `PageBuiltMessage` object
3. **Validate**: Check if page has already been audited
4. **Verify**: Ensure page is "landable" (accessible)

### 3. Audit Creation
If the page is eligible for audit:
1. **Create PageAuditRecord**: Initialize with default audit types
2. **Link to Domain**: Associate with domain audit record
3. **Set Status**: Mark as `BUILDING_PAGE`
4. **Save**: Persist to Neo4j database

### 4. Message Publishing
Publishes `PageAuditMessage` to Pub/Sub topic containing:
- Account ID
- Page Audit Record ID

### 5. Audit Execution
Other microservices consume the audit messages and perform:
- Visual analysis using Selenium and image processing
- Content analysis using NLP and text processing
- Accessibility evaluation
- SEO and metadata analysis

## Configuration

### Environment Variables
```properties
# Server Configuration
server.port=8080
management.server.port=80

# Neo4j Configuration
spring.data.neo4j.uri=NEO4J_BOLT_URI
spring.data.neo4j.username=NEO4J_USERNAME
spring.data.neo4j.password=NEO4J_PASSWORD
spring.data.neo4j.database=NEO4J_DATABASE_NAME

# Google Cloud Configuration
spring.cloud.gcp.project-id=PROJECT_ID
spring.cloud.gcp.credentials.location=GCP_CREDENTIALS_LOCATION

# Pub/Sub Topics
pubsub.page_audit_topic=PAGE_AUDIT_TOPIC
pubsub.audit_update=AUDIT_UPDATE_TOPIC
pubsub.error_topic=AUDIT_ERROR_TOPIC
```

### Resilience4j Configuration
The application includes comprehensive retry and circuit breaker configurations for:
- **Neo4j**: 10 retries with 10s delays
- **WebDriver**: 1 retry with 1s delay
- **GCP Services**: 5 retries with 5s delays
- **General HTTP**: 1 retry with 5s delay

## Getting Started

### Prerequisites
- Java 21
- Maven 3.9+
- Neo4j Database
- Google Cloud Platform account
- Docker (optional)

### Local Development

#### Install LookseeCore JAR to Local Maven Repository

Before building, you must install the LookseeCore JAR to your local Maven repository:

```bash
mvn install:install-file -Dfile=libs/core-0.3.0.jar -DgroupId=com.looksee -DartifactId=core -Dversion=0.3.0 -Dpackaging=jar
```

This ensures the core library is available for local builds and runs.

#### Build the Application
```bash
mvn clean install
```

#### Run Locally
```bash
java -ea -jar target/audit-manager-1.0.1.jar
```

**Note**: The `-ea` flag enables assertions for debugging.

#### Docker (Alternative)
```bash
# Build image
docker build --tag audit-manager .

# Run container
docker run -p 80:80 -p 8080:8080 --name audit-manager audit-manager
```

### Neo4j Setup

#### 1. Create Firewall Rules
```bash
gcloud compute firewall-rules create allow-neo4j-bolt-http-https \
  --allow tcp:7473,tcp:7474,tcp:7687 \
  --source-ranges 0.0.0.0/0 \
  --target-tags neo4j
```

#### 2. Create Neo4j Instance
```bash
gcloud config set project YOUR_PROJECT_ID
gcloud compute instances create neo4j-prod \
  --machine-type e2-medium \
  --image-project launcher-public \
  --image neo4j-community-1-4-3-6-apoc \
  --tags neo4j,http-server,https-server
```

#### 3. Configure Neo4j
Follow the [DigitalOcean Neo4j setup guide](https://www.digitalocean.com/community/tutorials/how-to-install-and-configure-neo4j-on-ubuntu-20-04) for detailed configuration steps.

### Deployment

#### Google Cloud Run
```bash
# Build and push to Google Container Registry
docker build --no-cache -t gcr.io/YOUR_PROJECT_ID/audit-manager:v1.0.1 .
docker push gcr.io/YOUR_PROJECT_ID/audit-manager:v1.0.1
```

#### Docker Hub
```bash
# Build and push to Docker Hub
docker build -t your-username/audit-manager:v1.0.1 .
docker push your-username/audit-manager:v1.0.1
```

## API Reference

### POST /
Receives page build notifications and initiates audits.

**Request Body**:
```json
{
  "message": {
    "data": "base64-encoded-message",
    "messageId": "message-id",
    "publishTime": "2023-01-01T00:00:00Z"
  }
}
```

**Response**:
- `200 OK`: Message processed successfully
- `500 Internal Server Error`: Processing error

## Testing

### Run Tests
```bash
mvn test
```

### Test Messages
Sample test messages are available in `src/test/resources/test_messages/UrlMessage` for:
- Domain page audit paths
- Single page audit paths

## Monitoring and Logging

### Logging
- Logs are written to `look-see.log`
- Log level: WARN (configurable)
- Console and file appenders enabled

### Health Checks
- Management endpoint: `http://localhost:80/actuator/health`
- Application endpoint: `http://localhost:8080/`

### Metrics
The application exposes Spring Boot Actuator endpoints for monitoring:
- Health status
- Application metrics
- Environment information

## Security

### SSL Certificate Generation
To generate a new PKCS12 certificate for SSL:

```bash
openssl pkcs12 -export -inkey private.key -in certificate.crt -out api_key.p12
```

### Environment Security
- Use Google Cloud Secret Manager for sensitive configuration
- Store credentials securely using environment variables
- Enable SSL/TLS for production deployments

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Support

For issues and questions:
- Create an issue on GitHub
- Check the [CHANGELOG.md](CHANGELOG.md) for recent updates
- Review the configuration examples in this README