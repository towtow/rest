package rest

import groovy.text.GStringTemplateEngine

import java.util.regex.Matcher
import java.util.regex.Pattern

class Router {
    private List<Map> routes = []

    def add(Class... resourceClasses) {
        resourceClasses.each { resourceClass ->
            Route r = resourceClass.getAnnotation(Route)
            routes << [template: r.value().replace('{', '${'), pattern: toPattern(r.value()), resourceClass: resourceClass]
            enhanceClass(resourceClass)
        }
    }

    private enhanceClass(resourceClass) {
        resourceClass.metaClass.url = {->
            makeURL(resourceClass, delegate.properties)
        }
        resourceClass.metaClass.static.url = { Class someResourceClass, Map params = [:] ->
            makeURL(someResourceClass, params)
        }
        def response = null
        resourceClass.metaClass.getResponse = {->
            response
        }
        resourceClass.metaClass.setResponse = { value ->
            response = value
        }
    }

    private toPattern(input) {
        Matcher m = (~/(\{)([a-zA-Z0-9]+)(\})/).matcher(input)
        StringBuffer r = new StringBuffer()
        while (m.find()) {
            m.appendReplacement(r, "(?<${m.group(2)}>[^/]+)")
        }
        m.appendTail(r)
        Pattern.compile(r.toString())
    }

    def matchResource(URI uri) {
        def res = null
        def params = [:]
        routes.each { entry ->
            def m = entry.pattern.matcher(uri.path)
            if (m.matches()) {
                if (res != null) throw new RuntimeException('Multiple routes match.') // todo: changing this to fail in add() would be better, but also harder...
                entry.pattern.namedGroups().each { k, v -> params[k] = m.group(v) }
                res = entry.resourceClass.newInstance(params)
            }
        }
        res
    }

    private String makeURL(resourceClass, params = [:]) {
        fillTemplate(routes.find { it.resourceClass == resourceClass }, params).toString()
    }

    private URI fillTemplate(entry, params) {
        def e = new GStringTemplateEngine()
        def r = e.createTemplate(entry.template as String).make(params).toString()
        URI.create("http://localhost:8080${r}")
    }
}
