[![Codacy Badge](https://app.codacy.com/project/badge/Grade/e2376d355755402aaa5bf7c533750851)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=deepthought42/WebTestVisualizer&amp;utm_campaign=Badge_Grade)

# Getting Started

## Launch Jar locally


### Command Line Interface(CLI)

maven clean install

java -ea -jar target/Look-see-#.#.#.jar

NOTE: The `-ea` flag tells the java compiler to run the program with assertions enabled

### Neo4j application setup

Note that this section will need to be replaced once we have an Anthos or Terraform script. 

Step 1: setup firewall for neo4j

	gcloud compute firewall-rules create allow-neo4j-bolt-http-https --allow tcp:7473,tcp:7474,tcp:7687 --source-ranges 0.0.0.0/0 --target-tags neo4j
	
Step 2: Get image name for Community version 1.4

 	gcloud compute images list --project launcher-public | grep --extended-regexp "neo4j-community-1-4-.*"
 	
Step 3: create new instance

gcloud config set project cosmic-envoy-280619
gcloud compute instances create neo4j-prod --machine-type e2-medium --image-project launcher-public --image neo4j-community-1-4-3-6-apoc --tags neo4j,http-server,https-server


gcloud compute instances add-tags neo4j-stage --tags http-server,https-server

Step 4 : SSH to server and check status

gcloud compute ssh neo4j-stage
sudo systemctl status neo4j

Follow step 3 from this webpage to configure neo4j server - https://www.digitalocean.com/community/tutorials/how-to-install-and-configure-neo4j-on-ubuntu-20-04

Step 6: Delete neo4j instance

gcloud compute instances delete neo4j-stage


### Docker

maven clean install

docker build --tag look-see .

docker run -p 80:80 -p 8080:8080 -p 9080:9080 -p 443:443 --name look-see look-see


### Deploy docker container to gcr

docker build --no-cache -t gcr.io/cosmic-envoy-280619/look-see-api:v#.#.# .

docker push gcr.io/cosmic-envoy-280619/look-see-api:v#.#.#



# Security

## Generating a new PKCS12 certificate for SSL

Run the following command in Linux to create a keystore called api_key with a privateKeyEntry

openssl pkcs12 -export -inkey private.key -in certificate.crt -out api_key.p12