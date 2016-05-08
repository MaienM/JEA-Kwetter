package servlets;

import flexjson.JSONSerializer;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import services.KwetterService;
import servlets.endpoints.Endpoint;
import servlets.endpoints.EndpointResponse;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "RESTServlet", urlPatterns = "/rest-api/*")
public class RESTServlet extends HttpServlet {
    @PersistenceContext(unitName = "main")
    private EntityManager em;

    @Inject private KwetterService service;

    private static Logger logger = Logger.getLogger(RESTServlet.class.getName());

    private static Map<Endpoint.Method, Map<Pattern, Method>> endpoints;

    private static Map<Pattern, Method> getEndpoints(Endpoint.Method endpointMethod) {
        if (endpoints == null) {
            endpoints = new HashMap<>();
        }
        if (!endpoints.containsKey(endpointMethod)) {
            Map<Pattern, Method> methods = new HashMap<>();
            endpoints.put(endpointMethod, methods);

            // Get the annotated methods.
            Reflections r = new Reflections("servlets.endpoints", new MethodAnnotationsScanner());
            for (Method method : r.getMethodsAnnotatedWith(Endpoint.class)) {
                Endpoint endpoint = method.getAnnotation(Endpoint.class);
                if (endpoint == null || endpoint.method() != endpointMethod) continue;
                methods.put(Pattern.compile("^" + endpoint.value() + "/?$"), method);
            }
        }

        return endpoints.get(endpointMethod);
    }

    private void doGeneric(HttpServletRequest request, HttpServletResponse response, Endpoint.Method endpointMethod) throws IOException {
        // Get the endpoints.
        Map<Pattern, Method> methods = getEndpoints(endpointMethod);

        // The response data object.
        Object responseData = null;

        // Try to find a matching endpoint.
        String path = request.getPathInfo();
        Boolean matchingRoute = false;
        for (Map.Entry<Pattern, Method> entry : methods.entrySet()) {
            // See if the endpoint matches.
            Matcher matcher = entry.getKey().matcher(path);
            if (matcher == null || !matcher.matches()) continue;

            // Match found, store.
            matchingRoute = true;

            // Get the parameters.
            String[] params = new String[matcher.groupCount()];
            for (int i = 0; i < matcher.groupCount(); i++) {
                params[i] = matcher.group(i + 1);
            }

            // Invoke the method.
            try {
                //Object t = entry.getValue().getDeclaringClass().newInstance();
                responseData = entry.getValue().invoke(null, request, service, params);
            } catch (IllegalAccessException | InvocationTargetException | InstantiationError e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                responseData = new EndpointResponse(EndpointResponse.Status.INTERNAL_SERVER_ERROR, "internal server error");
                //responseData = e;
            }
            break;
        }

        // 404
        if (!matchingRoute) {
            responseData = new EndpointResponse(EndpointResponse.Status.NOT_FOUND, "not found");
        }

        // EndpointResponse
        if (responseData instanceof EndpointResponse) {
            response.setStatus(((EndpointResponse) responseData).getStatus().getCode());
            responseData = ((EndpointResponse) responseData).getData();
        }

        // CORS headers.
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");

        // Convert to json and return.
        JSONSerializer serializer = new JSONSerializer();
        serializer.exclude("*.class");
        serializer.deepSerialize(responseData, response.getWriter());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGeneric(request, response, Endpoint.Method.GET);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGeneric(request, response, Endpoint.Method.POST);
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGeneric(request, response, Endpoint.Method.PUT);
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGeneric(request, response, Endpoint.Method.DELETE);
    }
}
