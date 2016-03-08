/*
 * @#JettyEmbeddedAppServer.java - 2016
 * Copyright bitDubai.com., All rights reserved.
 * You may not modify, use, reproduce or distribute this software.
 * BITDUBAI/CONFIDENTIAL
 */
package com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1.structure.jetty;

import com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1.structure.jetty.util.ConfigurationManager;
import com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1.structure.jetty.vpn.VpnWebSocketServlet;
import com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1.structure.jetty.vpn.WebSocketVpnServerChannel;
import com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1.structure.jetty.webservices.ApplicationResources;
import com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1.structure.jetty.webservices.security.SecurityFilter;

import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Slf4jLog;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.UUID;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

/**
 * The class <code>com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.server.developer.bitdubai.version_1.structure.jetty.JettyEmbeddedAppServer</code>
 * is the application web server to deploy the web socket server</p>
 * <p/>
 * Created by Roberto Requena - (rart3001@gmail.com) on 08/01/16.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class JettyEmbeddedAppServer {

    /**
     * Represent the logger instance
     */
    private static Logger LOG = Logger.getLogger(ClassUtils.getShortClassName(JettyEmbeddedAppServer.class));

    /**
     * Represent the DEFAULT_PORT value (9090)
     */
    public static final int DEFAULT_PORT = 9090;

    /**
     * Represent the DEFAULT_CONTEXT_PATH value (/fermat)
     */
    public static final String DEFAULT_CONTEXT_PATH = "/fermat";

    /**
     * Represent the WEB_APP_CONTEXT_PATH value (/fermat_web)
     */
    public static final String WEB_APP_CONTEXT_PATH = "/fermat_web";

    /**
     * Represent the JettyEmbeddedAppServer instance
     */
    private static JettyEmbeddedAppServer instance;

    /**
     * Represent the server instance
     */
    private Server server;

    /**
     * Represent the web socket server container instance
     */
    private ServerContainer wsServerContainer;

    /**
     * Represent the ServletContextHandler instance
     */
    private ServletContextHandler servletContextHandler;

    /**
     * Represent the ServerConnector instance
     */
    private ServerConnector serverConnector;

    /**
     * Constructor
     */
    private JettyEmbeddedAppServer(){
        super();
    }

    /**
     * Initialize and configure the server instance
     *
     * @throws IOException
     * @throws DeploymentException
     * @throws ServletException
     */
    private void initialize() throws IOException, DeploymentException, ServletException, URISyntaxException {

        LOG.info("Initializing the internal Server");

        Log.setLog(new Slf4jLog(Server.class.getName()));

        /*
         * Create and configure the server
         */
        this.server = new Server();
        this.serverConnector = new ServerConnector(server);
        String port = ConfigurationManager.getValue(ConfigurationManager.PORT);

        LOG.info("Server configure port = "+port);

        this.serverConnector.setPort(new Integer(port.trim()));
        this.server.addConnector(serverConnector);

        /*
         * Setup the basic application "context" for this application at "/fermat"
         */
        this.servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        this.servletContextHandler.setContextPath(JettyEmbeddedAppServer.DEFAULT_CONTEXT_PATH);
        this.servletContextHandler.setClassLoader(JettyEmbeddedAppServer.class.getClassLoader());
        this.server.setHandler(servletContextHandler);

        String resourceBase = "";
        URL webAppUri = this.getClass().getClassLoader().getResource("webapp");
        LOG.info("WebAppUri = "+webAppUri);

        if (webAppUri != null) {
            resourceBase = webAppUri.toURI().toASCIIString();
        }

        /*
         * Initialize web layer
         */
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath(JettyEmbeddedAppServer.DEFAULT_CONTEXT_PATH);
        webAppContext.setDescriptor(resourceBase + "/WEB-INF/web.xml");
        webAppContext.setResourceBase(resourceBase);
        webAppContext.addBean(new ServletContainerInitializersStarter(webAppContext), true);
        webAppContext.setWelcomeFiles(new String[]{"index.html"});
        webAppContext.addFilter(SecurityFilter.class, "/api/admin/*", EnumSet.of(DispatcherType.REQUEST));
        server.setHandler(webAppContext);

        /*
         * Initialize restful service layer
         */
        ServletHolder restfulServiceServletHolder = new ServletHolder(new HttpServlet30Dispatcher());
        restfulServiceServletHolder.setInitParameter("javax.ws.rs.Application", ApplicationResources.class.getName());
        restfulServiceServletHolder.setInitParameter("resteasy.use.builtin.providers", "true");
        webAppContext.addServlet(restfulServiceServletHolder, "/api/*");

        /*
         * Initialize javax.websocket layer
         */
        this.wsServerContainer = WebSocketServerContainerInitializer.configureContext(webAppContext);

        /*
         * Add WebSocket endpoint to javax.websocket layer
         */
        this.wsServerContainer.addEndpoint(WebSocketCloudServerChannel.class);
        this.wsServerContainer.addEndpoint(WebSocketVpnServerChannel.class);

        this.server.dump(System.err);


    }

    /**
     * Start the server instance
     *
     * @throws Exception
     */
    public void start() throws Exception {


       /* Inet4Address address;
        try {
            address = getIPv4Address("eth0");
            // TfsClientSingleton.init(address, tfsCache);
        } catch (UnknownHostException | SocketException e) {
            throw new Error(e);
        }

       /* PortMapping desiredMapping = new PortMapping(
                DEFAULT_PORT,
                address.getHostAddress(),
                PortMapping.Protocol.TCP
        );

        UpnpService upnpService = new UpnpServiceImpl(
                new PortMappingListener(desiredMapping)
        );

        upnpService.getControlPoint().search();*/

        this.initialize();
        LOG.info("Starting the internal server");
        this.server.start();

        LOG.info("Server URI = " + this.server.getURI());
        this.server.join();

    }

    private static Inet4Address getIPv4Address(String iface) throws SocketException, UnsupportedAddressTypeException, UnknownHostException {
        if (iface != null) {
            NetworkInterface networkInterface = NetworkInterface.getByName(iface);
            Enumeration<NetworkInterface> enume =NetworkInterface.getNetworkInterfaces();
            while(enume.hasMoreElements()){
                System.out.println(enume.nextElement().getDisplayName());
            }
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if (addr instanceof Inet4Address) {
                    return (Inet4Address) addr;
                }
            }
        }

        InetAddress localhost = InetAddress.getLocalHost();
        if (localhost instanceof Inet4Address) {
            return (Inet4Address) localhost;
        }

        throw new UnsupportedAddressTypeException();
    }

    /**
     * Deploy a new vpn web socket
     *
     * @return path
     */
    @Deprecated
    public String deployNewVpnWebSocket() throws Exception {

        // Add a websocket to a specific path spec
        String id = UUID.randomUUID().toString();
        String path =  "/" + id + "/*";
        System.out.println("Deploy a new ws path = " + path);
        ServletHolder servletHolder = new ServletHolder("vpn_"+id, VpnWebSocketServlet.class);
        servletHolder.setInitOrder(1);
        instance.servletContextHandler.addServlet(servletHolder, path);
        return path;
    }

    /**
     * Deploy a new vpn web socket
     *
     * @return path
     */
    @Deprecated
    public String deployNewJavaxVpnWebSocket() throws Exception {

        // Add a websocket to a specific path spec
        String id = UUID.randomUUID().toString();
        String path = "/" + id + "/vpn/";
        System.out.println("Deploy a new ws path = " + path);
        ServerEndpointConfig serverEndpointConfig = ServerEndpointConfig.Builder.create(WebSocketVpnServerChannel.class, path).build();
        instance.wsServerContainer.addEndpoint(serverEndpointConfig);

        return path;
    }


    /**
     * Get the instance value
     *
     * @return instance current value
     */
    public static JettyEmbeddedAppServer getInstance() {

        if (instance == null){
            instance = new JettyEmbeddedAppServer();
        }

        return instance;
    }

    /**
     * Get the server value
     *
     * @return server current value
     */
    public Server getServer() {
        return server;
    }

}
