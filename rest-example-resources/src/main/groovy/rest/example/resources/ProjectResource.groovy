package rest.example.resources

import rest.Route

@Route('/project/{id}/asdasd/{moreId}')
class ProjectResource {
    def id
    def moreId

    def get() {
        response.body = [
                gotId: id,
                moreIdIs: moreId,
                refTOSelf: url()
        ]
    }

    def post() {}

    def delete() {}
}
