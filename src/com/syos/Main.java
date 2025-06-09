package com.syos;

import com.syos.interfaces.cli.ApplicationBuilder;
import com.syos.interfaces.cli.CLISessionController;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        CLISessionController cliSession = ApplicationBuilder.buildCLIApp();
        cliSession.run();
    }
}