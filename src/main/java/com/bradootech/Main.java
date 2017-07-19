package com.bradootech;

import com.google.gson.Gson;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static java.util.Arrays.asList;

public class Main {

    public static void main(String[] args) {
        try {

            /**
             * Start connection get info data
             */
            XmlRpcClient client = new XmlRpcClient();

            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();

            config.setServerURL(new URL("https://demo.odoo.com/start"));

            Map<String, String> info = (Map<String, String>) client
                    .execute(config, "start", Collections.emptyList());

            Credential c = new Credential(info.get("host"), info.get("database"),
                    info.get("user"), info.get("password"));

            Gson converter = new Gson();

            System.out.println(converter.toJson(c));

            /**
             * Login
             */

            XmlRpcClientConfigImpl common_config =
                    new XmlRpcClientConfigImpl();
            common_config.setServerURL(new URL(String
                    .format("%s/xmlrpc/2/common", c.url)));
            Object o = client.execute(common_config, "version",
                    Collections.emptyList());

            System.out.println(converter.toJson(o));

            int uid = (int) client
                    .execute(common_config, "authenticate",
                            asList(c.db, c.username, c.password,
                                    Collections.emptyMap()));

            System.out.println("User: " + uid);

            /**
             * Calling Methods
             */

            XmlRpcClient models = new XmlRpcClient() {{
                setConfig(new XmlRpcClientConfigImpl() {{
                    setServerURL(new URL(String.format("%s/xmlrpc/2/object",
                            c.url)));
                }});
            }};

            o = models.execute("execute_kw", asList(
                    c.db, uid, c.password,
                    "res.partner", "check_access_rights",
                    asList("read"),
                    new HashMap() {{
                        put("raise_exception", false);
                    }}
            ));

            System.out.println(converter.toJson(o));

            /**
             * List records
             */

            List<Object> l = asList((Object[]) models.execute("execute_kw",
                    asList(c.db, uid, c.password, "res.partner",
                            "search", asList(asList(
                                    asList("is_company", "=", true),
                                    asList("customer", "=", true)))
                    )));

            System.out.println(converter.toJson(l));

            /**
             * List with pagination
             */

            l = asList((Object[]) models.execute("execute_kw",
                    asList(c.db, uid, c.password, "res.partner",
                            "search", asList(asList(
                                    asList("is_company", "=", true),
                                    asList("customer", "=", true))),
                            new HashMap() {{
                                put("offset", 3);
                                put("limit", 4);
                            }}
                    )));

            System.out.println(converter.toJson(l));

            /**
             * Counting records server side
             */

            Integer count = (Integer) models.execute("execute_kw", asList(
                    c.db, uid, c.password, "res.partner", "search_count",
                    asList(asList(
                            asList("is_company", "=", true),
                            asList("customer", "=", true)))
            ));

            System.out.println(count);

            /**
             * Read records
             */

            List ids = asList((Object[]) models.execute(
                    "execute_kw", asList(
                            c.db, uid, c.password,
                            "res.partner", "search",
                            asList(asList(
                                    asList("is_company", "=", true),
                                    asList("customer", "=", true))),
                            new HashMap() {{
                                put("limit", 1);
                            }})));
            Map record = (Map) ((Object[]) models.execute(
                    "execute_kw", asList(
                            c.db, uid, c.password,
                            "res.partner", "read",
                            asList(ids)
                    )
            ))[0];

            System.out.println(converter.toJson(record));

            /**
             * Read Record on specific fields
             */
            l = asList((Object[]) models.execute("execute_kw", asList(
                    c.db, uid, c.password,
                    "res.partner", "read",
                    asList(ids),
                    new HashMap() {{
                        put("fields", asList("name", "country_id", "comment"));
                    }}
            )));

            System.out.println(converter.toJson(l));


            Map m = (Map<String, Map<String, Object>>)
                    models.execute("execute_kw", asList(
                            c.db, uid, c.password,
                            "res.partner", "fields_get",
                            Collections.emptyList(),
                            new HashMap() {{
                                put("attributes",
                                        asList("string", "help", "type"));
                            }}
                    ));

            System.out.println(converter.toJson(m));

            /**
             * Search and read record list
             */

            l = asList((Object[]) models.execute("execute_kw", asList(
                    c.db, uid, c.password,
                    "res.partner", "search_read",
                    asList(asList(
                            asList("is_company", "=", true),
                            asList("customer", "=", true))),
                    new HashMap() {{
                        put("fields", asList("name", "country_id", "comment"));
                        put("limit", 5);
                    }}
            )));

            System.out.println(converter.toJson(l));

            /**
             * Create a record
             */

            Map user = new HashMap() {{
                put("name", "New Partner");
                put("country_id", 2);
                put("comment", "Novo usuário para teste  =)");
            }};

            Integer id = (Integer) models.execute("execute_kw", asList(
                    c.db, uid, c.password,
                    "res.partner", "create",
                    asList(user)
            ));

            System.out.println(id);

            /**
             * Update a record (a created one =))
             */

            Map userUpdate = new HashMap() {{
                put("name", "Awesome Newer");
            }};

            o = models.execute("execute_kw", asList(
                    c.db, uid, c.password,
                    "res.partner", "write",
                    asList(asList(id), userUpdate)
            ));

            System.out.println(converter.toJson(o));

            // get record name after having changed it
            l = asList((Object[]) models.execute("execute_kw", asList(
                    c.db, uid, c.password,
                    "res.partner", "name_get",
                    asList(asList(id))
            )));

            System.out.println(converter.toJson(l));

            /**
             * Delete Record
             */

            models.execute("execute_kw", asList(
                    c.db, uid, c.password,
                    "res.partner", "unlink",
                    asList(asList(id))));
            // check if the deleted record is still in the database
            l = asList((Object[]) models.execute("execute_kw", asList(
                    c.db, uid, c.password,
                    "res.partner", "search",
                    asList(asList(asList("id", "=", 78)))
            )));

            System.out.println(converter.toJson(l));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (XmlRpcException e2) {
            e2.printStackTrace();
        }


    }
}
