package com.xcat;

import static spark.Spark.*;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static spark.debug.DebugScreen.enableDebugScreen;
import net.sf.saxon.s9api.*;


public class HelloWorld {
    static Processor processor = new Processor(false);


	public static void main(String[] args) throws Exception {

        XdmNode booksDoc = processor.newDocumentBuilder().build(new File("database.xml"));

        String[] resultsNames = new String[] {"title", "author", "description", "image"};
        String[] xversions = new String[] {"1.0", "2.0", "3.0"};

		get("/", (req, res) -> {
            Map ctx = new HashMap();
            XPathCompiler compiler = processor.newXPathCompiler();
            String selectedVersion = req.queryParams("xversion");

            ArrayList versionList = new ArrayList();

            for (String xversion: xversions){
                HashMap map = new HashMap();
                map.put("value", xversion);
                if (xversion.equals(selectedVersion)){
                    map.put("selected", true);
                }
                versionList.add(map);
            }

            ctx.put("xversions", versionList);

            if (selectedVersion != null){
                compiler.setLanguageVersion(selectedVersion);
            }

            ctx.put("xversion", selectedVersion);

            String query = req.queryParams("query");
		    if (query == null) {
                query = "";
            }

            ctx.put("query", query);

            ArrayList results = new ArrayList<String>();

            XPathSelector selector = compiler.compile("/root/books/book[contains(title/text(), '" + query + "')]").load();
            selector.setContextItem(booksDoc);
            for (XdmItem item: selector) {
                HashMap resultsMap = new HashMap<String, String>();

                for (String name: resultsNames) {
                    XdmNode thing = getChild((XdmNode)item, new QName(name));
                    resultsMap.put(name, thing.getStringValue());
                }

                results.add(resultsMap);
            }

            ctx.put("results", results);

            return new ModelAndView(ctx, "index.hbs");
        }, new HandlebarsTemplateEngine());

        enableDebugScreen();
	}

    private static XdmNode getChild(XdmNode parent, QName childName) {
        XdmSequenceIterator iter = parent.axisIterator(Axis.CHILD, childName);
        if (iter.hasNext()) {
            return (XdmNode)iter.next();
        } else {
            return null;
        }
    }
}