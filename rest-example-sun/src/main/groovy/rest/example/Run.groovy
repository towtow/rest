package rest.example

import rest.Controller
import rest.Router
import rest.example.resources.FaviconResource
import rest.example.resources.ProjectResource
import rest.example.resources.ProjectsResource
import rest.example.resources.TestResource

def router = new Router([FaviconResource, ProjectsResource, ProjectResource, TestResource])
def controller = new Controller(router)
rest.sunhttpserver.RestServer.start(controller)
