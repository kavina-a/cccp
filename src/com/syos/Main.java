package com.syos;

import client.desktop.interfaces.cli.ApplicationBuilder;
import client.desktop.interfaces.cli.CLISessionController;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        CLISessionController cliSession = ApplicationBuilder.buildCLIApp();
        cliSession.run();
    }
}

