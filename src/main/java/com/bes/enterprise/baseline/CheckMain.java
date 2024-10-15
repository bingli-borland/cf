package com.bes.enterprise.baseline;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import org.w3c.dom.Document;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CheckMain {

    private static GroovyScriptEngine engine = null;

    /**
     * Groovy脚本import包
     */
    private static final String GROOVY_IMPORTS = "import groovy.xml.XmlSlurper;";


    public static void main(String[] args) throws Exception {

        Map<String, Object> params = new HashMap<>();
        params.put("document", new File("src/main/resources/xml/server.config"));
        Object server = executeGroovyScript("return new XmlSlurper().parse(document)", params);
        params.clear();
        params.put("server", server);
        String[] scripts = new File("src/main/resources/groovy").list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".groovy");
            }
        });
        for (String script : scripts) {
            executeGroovyScriptByPath(script, params);
        }

    }

    public static Document parseXML() throws Exception {
        String xml = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(xml.getBytes()));
    }

    /**
     * 执行Groovy脚本
     *
     * @param script
     * @param param
     * @return
     * @throws Exception
     */
    public static Object executeGroovyScript(String script, Map<String, Object> param) throws Exception {
        checkNotNull(script, "Groovy script can NOT be null");
        // Groovy脚本import包 + 执行脚本
        String evalScript = GROOVY_IMPORTS + script;
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("groovy");
        Bindings bindings = engine.createBindings();
        bindings.putAll(param);
        return engine.eval(evalScript, bindings);
    }

    /**
     * 执行Groovy脚本
     *
     * @param script
     * @param param
     * @return
     * @throws Exception
     */
    public static Object executeGroovyScriptByPath(String script, Map<String, Object> param) throws Exception {
        checkNotNull(script, "Groovy script can NOT be null");
        GroovyScriptEngine engine = groovyScriptEngine();
        Binding binding = new Binding();
        for (String key : param.keySet()) {
            binding.setVariable(key, param.get(key));
        }
        return engine.run(script, binding);
    }

    /**
     * 获取ScriptEngine
     *
     * @return ScriptEngine
     */
    private static GroovyScriptEngine groovyScriptEngine() throws IOException {
        if (engine == null) {
            engine = new GroovyScriptEngine("src/main/resources/groovy");
        }
        return engine;
    }

    /**
     * checkNotNull
     *
     * @param reference
     * @param errorMessage
     * @throws IllegalArgumentException
     */
    public static <T> T checkNotNull(T reference, String errorMessage) throws IllegalArgumentException {
        if (reference == null)
            throw new IllegalArgumentException(errorMessage);
        return reference;
    }
}
