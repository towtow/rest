package rest.example.resources

import rest.Route

@Route('/projects')
class ProjectsResource {

    def get() {
        response.body = [
                result: [
                        something: 'get projects',
                        andmore: 123123,
                        andalist: [1, 2, 3, 'ads', null],
                        aLink: url(ProjectResource, [id: 4711, moreId: 234]),
                        anotherLink: new ProjectResource([id: 4712, moreId: 234]).url()
                ]
        ]
    }

    def post() {

    }
}
