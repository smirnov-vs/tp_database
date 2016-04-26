package main;

import db.Database;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import rest.*;

/**
 * Created by vladislav on 18.03.16.
 */
public class Main {

    private static class DatabaseAbstractBinder extends AbstractBinder {
        private final Database database;

        DatabaseAbstractBinder(Database database) {
            this.database = database;
        }

        @Override
        protected void configure() {
            bind(database).to(Database.class);
        }
    }

    public static boolean isNumeric(String s) {
        return s.matches("\\d+");
    }

    @SuppressWarnings("OverlyBroadThrowsClause")
    public static void main(String[] args) throws Exception {
        int port = -1;
        if (args.length == 1 && isNumeric(args[0])) {
            port = Integer.valueOf(args[0]);
        } else {
            System.err.println("Specify port");
            System.exit(1);
        }

        Database database = new Database();

        System.out.append("Jetty starting at port: ").append(String.valueOf(port)).append('\n');

        final Server server = new Server(port);
        final ServletContextHandler contextHandler = new ServletContextHandler(server, "/db/api", ServletContextHandler.SESSIONS);
        final ResourceConfig config = new ResourceConfig(General.class, Forum.class, ForumThread.class, Post.class, User.class);
        config.register(new DatabaseAbstractBinder(database));
        contextHandler.addServlet(new ServletHolder(new ServletContainer(config)), "/*");
        server.setHandler(contextHandler);

        server.start();
        server.join();
    }
}