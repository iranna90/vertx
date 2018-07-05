package nl.revolution.vertx3.eventbus.basic;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class EchoServiceVerticle extends AbstractVerticle {

    public static final String ADDRESS = "echo-service";
    private static final Logger LOG = LoggerFactory.getLogger(EchoServiceVerticle.class);

    @Override
    public void start() {
        LOG.info("Starting echo-service");

        vertx.eventBus().consumer(EchoServiceVerticle.ADDRESS, message -> {
            JsonObject messageBody = (JsonObject)message.body();

            messageBody.put("passedThrough", "echo-service");
            LOG.info("Sending reply: " + messageBody);

            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            message.reply(messageBody);
            LOG.info("Reply sent");
        });
    }
}
