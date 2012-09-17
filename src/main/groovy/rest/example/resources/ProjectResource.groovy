package rest.example.resources

import rest.Route

@Route('/project/{id}')
class ProjectResource {
    def id

    def get() {
        response.body = [
                gotId: id
        ]
    }
}
