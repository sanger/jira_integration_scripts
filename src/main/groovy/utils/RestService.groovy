/**
 * See README.md for copyright details
 */
package utils

import exceptions.RestServiceException
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.*

/**
 * The {@code RestService} class represents a utility class that can communicate
 * with REST services with GET/POST/UPDATE HTTP methods.
 *
 * @author ke4
 *
 */
class RestService {

    def http

    RestService(serviceUrl) {
        http = new HTTPBuilder(serviceUrl)
    }

    private String request(Map params) {
        http.request(params.requestType) {
            uri.path = params.path
            uri.query = params.query
            requestContentType = JSON
            body = params.json

            response.sucess = { resp ->
                resp.getEntity().getContent().text
            }

            response.failure = { resp ->
                def slurper = new JsonSlurper()
                def errors = slurper.parseText(resp.getEntity().getContent().text)
                def errorMessage = errors.collect { key, value -> "${key.capitalize()} ${value.join(', ')}" }.join(".${System.getProperty('line.separator')}")
                throw new RestServiceException(errorMessage)
            }
        }
    }

    def post(path, payload) {
        request(requestType: POST, path: path, json: payload)
    }

    def get(path, queryParams) {
        request(requestType: GET, path: path, query: queryParams)
    }

    def put(path, payload) {
        request(requestType: PUT, path: path, json: payload)
    }
}
