package main.java.com.looksee.actors;

import main.java.com.looksee.api.Logger;

@Controller
public class PubSubController {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

  @RequestMapping(value = "/", method = RequestMethod.POST)
  public ResponseEntity receiveMessage(@RequestBody Body body) {
    // Get PubSub message from request body.
    Body.Message message = body.getMessage();
    if (message == null) {
      String msg = "Bad Request: invalid Pub/Sub message format";
      System.out.println(msg);
      return new ResponseEntity(msg, HttpStatus.BAD_REQUEST);
    }

    String data = message.getData();
    String target =
        !StringUtils.isEmpty(data) ? new String(Base64.getDecoder().decode(data)) : "World";
    String msg = "Hello " + target + "!";

    System.out.println(msg);
    return new ResponseEntity(msg, HttpStatus.OK);
  }
}
