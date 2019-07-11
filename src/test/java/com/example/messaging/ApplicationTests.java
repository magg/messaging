package com.example.messaging;


import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SocketOptions;
import com.example.messaging.controller.ChatController;
import com.example.messaging.domain.Chat;
import com.example.messaging.domain.Message;
import com.example.messaging.domain.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nosan.embedded.cassandra.Cassandra;
import com.github.nosan.embedded.cassandra.Settings;
import com.github.nosan.embedded.cassandra.cql.CqlScript;
import com.github.nosan.embedded.cassandra.local.LocalCassandraFactory;
import com.sun.net.httpserver.HttpServer;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.example.messaging.Configuration.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;


@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApplicationTests {

    private static Cassandra cassandra;
    private static HttpServer server;


    @BeforeClass
    public static void setUp() throws Exception {

        LocalCassandraFactory cassandraFactory = new LocalCassandraFactory();
        cassandra = cassandraFactory.create();
        cassandra.start();

        try {
            Settings settings = cassandra.getSettings();
            executeScripts(settings);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        getFileReader().loadContacts();

        server = HttpServer.create(new InetSocketAddress(80), 0);
        ChatController chatHandler = new ChatController(getChatService(), getObjectMapper(),
                getErrorHandler(), getFileReader(), getUserService(), getMessageService());
        server.createContext("/chats", chatHandler::handle);
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        Thread.sleep(30*1000);

    }


    @Test
    public void findUsers(){

       Optional<User> u1 = getUserService().findOne(51201L);
       Optional<User> u2 = getUserService().findOne(98302L);
       Optional<User> u3 = getUserService().findOne(1L);

       assertTrue(u1.isPresent());
       assertTrue(u2.isPresent());
       assertFalse(u3.isPresent());

    }


    @Test
    public void testChatCreatedInvalidSchema()
            throws ClientProtocolException, IOException {


        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("chats/badSchemaChat.json").getFile());

        String json = readFile(file.getAbsolutePath(), StandardCharsets.UTF_8);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://127.0.0.1:80/chats");
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");


        CloseableHttpResponse response = client.execute(httpPost);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(400));

    }

    @Test
    public void testChatCreatedValidSchema()
            throws ClientProtocolException, IOException {


        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("chats/goodSchemaChat.json").getFile());

        String json = readFile(file.getAbsolutePath(), StandardCharsets.UTF_8);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://127.0.0.1:80/chats");
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");


        CloseableHttpResponse response = client.execute(httpPost);
        String lol = convert(response.getEntity().getContent(),StandardCharsets.UTF_8 );
        System.out.println(lol);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }


    @Test
    public void testChatInvalidContactList()
            throws ClientProtocolException, IOException {


        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("chats/invalidContactChat.json").getFile());

        String json = readFile(file.getAbsolutePath(), StandardCharsets.UTF_8);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://127.0.0.1:80/chats");
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");


        CloseableHttpResponse response = client.execute(httpPost);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(400));


    }

    @Test
    public void testSendMessages() throws ClientProtocolException, IOException {


        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("message/message1.json").getFile());

        String json = readFile(file.getAbsolutePath(), StandardCharsets.UTF_8);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://127.0.0.1:80/chats/13079/messages");
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");


        CloseableHttpResponse response = client.execute(httpPost);
        assertEquals(200, response.getStatusLine().getStatusCode());

    }


    @Test
    public void testSendMessage2() throws ClientProtocolException, IOException {


        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("message/message2.json").getFile());

        String json = readFile(file.getAbsolutePath(), StandardCharsets.UTF_8);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://127.0.0.1:80/chats/13079/messages");
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");


        CloseableHttpResponse response = client.execute(httpPost);
        assertEquals(200, response.getStatusLine().getStatusCode());

    }

    @Test
    public void testSendMessage3() throws ClientProtocolException, IOException {


        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("message/message3.json").getFile());

        String json = readFile(file.getAbsolutePath(), StandardCharsets.UTF_8);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://127.0.0.1:80/chats/13079/messages");
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");


        CloseableHttpResponse response = client.execute(httpPost);
        assertEquals(200, response.getStatusLine().getStatusCode());

    }


    @Test
    public void testSentMessageListByOrder() throws ClientProtocolException, IOException {

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet( "http://127.0.0.1:80/chats/13079/messages");

        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(request);

        ObjectMapper mapper = new ObjectMapper();

        String content = convert(response.getEntity().getContent(),StandardCharsets.UTF_8 );

        System.out.println(content);

        System.out.println(response.getStatusLine().getStatusCode());


        List<Message> myObjects = mapper.readValue(content, new TypeReference<List<Message>>(){});

        assertEquals(1562218675087L, myObjects.get(0).getTimestamp().longValue());
        assertEquals(1562218675085L, myObjects.get(1).getTimestamp().longValue());
        assertEquals(1562218675082L, myObjects.get(2).getTimestamp().longValue());

    }


    @Test
    public void testUserChatList()throws ClientProtocolException, IOException {

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet( "http://127.0.0.1:80/chats?userId=86487");

        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(request);

        ObjectMapper mapper = new ObjectMapper();

        String content = convert(response.getEntity().getContent(),StandardCharsets.UTF_8 );

        System.out.println(content);

        System.out.println(response.getStatusLine().getStatusCode());


        List<Chat> myObjects = mapper.readValue(content, new TypeReference<List<Chat>>(){});

        assertFalse(myObjects.isEmpty());
        assertEquals("13079", myObjects.get(0).getId().toString());

    }


    @AfterClass
    public static void tearDown() throws Exception { server.stop(3);
        cassandra.stop();
    }


    //com.datastax.cassandra:cassandra-driver-core:3.7.1
    private static void executeScripts(Settings settings) {
        SocketOptions socketOptions = new SocketOptions();
        socketOptions.setConnectTimeoutMillis(30000);
        socketOptions.setReadTimeoutMillis(30000);
        try (Cluster cluster = Cluster.builder().addContactPoints(settings.getAddress())
                .withPort(settings.getPort()).withSocketOptions(socketOptions)
                .withoutJMXReporting().withoutMetrics()
                .build()) {
            Session session = cluster.connect();
            List<String> statements = CqlScript.classpath("schema.cql").getStatements();
            statements.forEach(session::execute);
        }
    }

    String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public String convert(InputStream inputStream, Charset charset) throws IOException {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }




}
