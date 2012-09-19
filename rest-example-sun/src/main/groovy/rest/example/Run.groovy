package rest.example

import rest.Controller
import rest.Router
import rest.example.resources.ProjectResource
import rest.example.resources.ProjectsResource
import rest.example.resources.TestResource

def router = new Router([ProjectsResource, ProjectResource, TestResource])
def controller = new Controller(router)
rest.sunhttpserver.RestServer.start(controller)
