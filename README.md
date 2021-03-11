# Assignment
Repository for sharing the solution of the assignment solution

Additional questions:
### The test data contains a big amount of test data. If you would have an ability to put this to the repository, how would you structure it?

We can keep it on AEM repository (create a node under /etc, like /etc/oscarFilms and add data inside by creating child nodes (one node for one entry)). We can access the node to get the data from every nodes and provide response accordingly.
```
Map<String, Object> param = new HashMap<String, Object>();
param.put(ResourceResolverFactory.SUBSERVICE, "subService");
ResourceResolver resourceResolver = resolverFactory.getServiceResourceResolver(param);
Resource res = resourceResolver.getResource("/etc/oscarFilms");
```
We can use the data received from this node as oscars.json(as provided in the test) in our implementation. We can do all cRUD operations as and when needed.


### How can you improve the performance of the functionality that you're going to implement?

From my viewpoint,
1. If we have any database, keep data in a database(e.g., mongodb) and write a REST API to fetch the data based on the request parameters passed in the API. This API can be created as micro-service module. Here, we might need to implment extra APIs, if we need to modify(CRUD operations) the data as well. We can add a caching layer to improve performance as well.
2. We can use parallel streams in current implementation.
3. For design improvement, we can have separate filters for  parameter and use it while passing the paramFilters in the code.