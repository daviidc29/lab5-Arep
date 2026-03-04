package com.co.edu.escuelaing.lab5;

import java.io.IOException;
import java.net.URISyntaxException;

import com.co.edu.escuelaing.server.HttpServer;

import static com.co.edu.escuelaing.server.HttpServer.get;
import static com.co.edu.escuelaing.server.HttpServer.staticfiles;

public class App {

    public static void main(String[] args) throws IOException, URISyntaxException {
        staticfiles("/webroot/public");

        get("/App/hello", (req, res) -> "Hello " + req.getValues("name"));
        get("/App/pi", (req, res) -> String.valueOf(Math.PI));

        HttpServer.main(args);
    }
}