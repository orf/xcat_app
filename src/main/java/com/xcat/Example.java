package com.xcat;

import static spark.Spark.*;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.io.File;
import java.util.*;

import static spark.debug.DebugScreen.enableDebugScreen;
import net.sf.saxon.s9api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Example {
    private static Processor processor = new Processor(true);
    private static Logger logger = LoggerFactory.getLogger(Example.class);
    private static XdmNode booksDoc;

    public static void main(String[] args) throws Exception {
        booksDoc = processor.newDocumentBuilder().build(new File("database.xml"));

        String[] resultsNames = new String[] {"title", "author", "description", "image"};
        String[] xversions = new String[] {"1.0", "2.0", "3.0"};

        if (Arrays.asList(args).contains("--repl")){
            Scanner scan = new Scanner(System.in);

            while (true){
                System.out.print("Query: ");
                String query = scan.nextLine();
                XPathSelector selector;
                try {
                    selector = RunQuery(query, "3.0");
                } catch (SaxonApiException e){
                    continue;
                }
                System.out.print("Results:\n");
                for (XdmItem item: selector) {
                    System.out.print(item.toString() + "\n");
                }
                System.out.print("\n");
            }
        }

		get("/", (req, res) -> {
            Map ctx = new HashMap();
            String selectedVersion = req.queryParams("xversion");

            if (selectedVersion == null){
                selectedVersion = xversions[0];
            }

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

            ctx.put("xversion", selectedVersion);

            String query = req.queryParams("query");
		    if (query == null) {
                query = "";
            }

            ctx.put("query", query);

            ArrayList results = new ArrayList<String>();
            String xquery = "/root/books/book[contains(title/text(), '" + query + "')]";
            logger.info(xquery);

            for (XdmItem item: RunQuery(xquery, selectedVersion)) {
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

	private static XPathSelector RunQuery(String query, String version) throws SaxonApiException{
        XPathSelector selector;
        XPathCompiler compiler = processor.newXPathCompiler();
        compiler.setLanguageVersion(version);

        try {
            selector = compiler.compile(query).load();
        } catch (SaxonApiException e){
            logger.info("Error: " + e.toString());
            throw e;
        }

        selector.setContextItem(booksDoc);
        return selector;
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