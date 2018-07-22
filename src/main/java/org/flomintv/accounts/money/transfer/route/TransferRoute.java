package org.flomintv.accounts.money.transfer.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.flomintv.accounts.money.transfer.error.NotEnoughMoneyException;

public class TransferRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        onException(NotEnoughMoneyException.class)
                .handled(true)
                .process(exchange -> {
                    exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
                    exchange.getOut().setHeader(Exchange.CONTENT_TYPE, "text/plain");
                    NotEnoughMoneyException camelExceptionCaught = exchange.getProperty("CamelExceptionCaught", NotEnoughMoneyException.class);
                    exchange.getOut().setBody(camelExceptionCaught.getMessage(), String.class);
                });

        onException(Exception.class)
                .handled(true)
                .process(exchange -> {
                    exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
                    exchange.getOut().setHeader(Exchange.CONTENT_TYPE, "text/plain");
                    exchange.getOut().setBody("We are sorry, but your request can't be processed right now. Please try again later or contact our support team.", String.class);
                });

        from("cxfrs:http://localhost:8080?resourceClasses=org.flomintv.accounts.money.transfer.rest.RestTransferService&bindingStyle=SimpleConsumer&providers=jsonProvider")
            .toD("direct:${header.operationName}");

        /* Transfer service */
        from("direct:createTransfer")
            .bean("transferService", "createTransfer");

        from("direct:getTransfer")
            .bean("transferService", "getTransfer(${header.id})");

        from("direct:cancelTransfer")
            .bean("transferService", "cancelTransfer(${header.id})");


        /* User service */
        from("direct:getUser")
            .bean("userService", "getUser(${header.id})");

        /* Account service */
        from("direct:getAccount")
                .bean("accountService", "getAccount(${header.id})");

    }
}
