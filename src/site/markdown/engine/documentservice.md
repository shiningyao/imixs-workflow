# The DocumentService 

The _DocumentService_ provides the interface to store, load and query data objects (_documents_) within a database. The service EJB is based on the Java Persistence API (JPA) to store a _document_ into a SQL database and provides also a [Lucene Search Index](https://lucene.apache.org/) to query documents by a search term.  In addition the _DocumentService_ assigns each document with an access control list (ACL). The ACL protects a document from unauthorized access. In case the _CallerPrincipal_ has insufficient rights to access or modify a specific document, the _DocumentService_ throws an _AccessDeniedException_. 


##How to Store and Load a Document 

A Document in the Imixs-Workflow systems is represented by the [ItemCollection class](../core/itemcollection.html), which presents a generic value object used by all methods of the Imixs-Workflow engine. The _DocumentService_ provides methods to save and load a document.
 
The following example shows how an instance of an ItemCollection can be saved using the _DocumentService_:
 
	  @EJB
	  org.imixs.workflow.jee.ejb.DocumentService documentService;
	  //...
	
	  ItemCollection myDocument=new ItemCollection;
	  myDocument.replaceItemValue("type","product");
	  myDocument.replaceItemValue("name","coffee");
	  myDocument.replaceItemValue("weight",new Integer(500));
	  	
	  // save ItemCollection
	  myDocument=documentService.save(myDocument);

In this example a new ItemCollection is created and the properties 'type, 'name' and 'weight' are stored into a ItemCollection. The save() method stores the document into the database. If the document is stored the first time, the method generates an ID which can be used to identify the document for later access. This ID is provided in the property '$uniqueid' which will be added automatically by the _DocumentService_. If the document was saved before, the method updates only the items of the document in the database.
  
The next example shows how to use the $uniqueid of a stored ItemCollection to load the document from the database. For this the ID is passed to the load() method:
 
	  @EJB
	  org.imixs.workflow.jee.ejb.DocumentService documentService;
	  //...
	  // save document
	  myDocument=documentService.save(myDocument);
	  // get ID from ItemCollection 
	  String id=myDocument.getUniqueID();
	  // load the document from database
	  myDocument=documentService.load(id);
 
__Note:__ The method load() checks if the CallerPrincipal has read access to a document. If not, the method returns null. The method doesn't throw an AccessDeniedException if the user is not allowed to read the document. This is to prevent an aggressor with informations about the existence of that specific document.

### Creation and Modified Date 
The _DocumentService_ also creates TimeStamps to mark the creation and last modified date of a document. These properties are also part of the document returned by the save method. The items are named "$created" and "$modified".
 
	  //...
	  // save ItemCollection
	  myDocument=documentService.save(myDocument);
	  Date created=myDocument.getItemValueDate("$Created");
	  Date modified=myDocument.getItemValueDate("$Modified");



## Query Documents

The _DocumentService_ provides a [Lucene Index](https://lucene.apache.org/) to query documents by an individual search query. A document is automatically added into the index when the document is saved. 
The find() method of the _DocumentService_ can be used to query documents by a Lucene search term. 

The following example returns all documents starting with the search term 'Imixs':

    String serachTerm="(imixs*)";
    result=documentService.find(serachTerm);

To query for a specific subset of documents, it is also possible to add individual attributes to the search term. The follwoing example will return all documents with the serach term 'imixs' and the Type 'workitem':


    String serachTerm="(type:'workitem')(imixs*)";
    result=documentService.find(serachTerm);
        
    
See the [Lucene QueryParser description](https://lucene.apache.org/core/6_2_1/queryparser/org/apache/lucene/queryparser/classic/package-summary.html#package.description) for further details about the usage of lucene search terms. 
 

###Pagination
The _DocumentService_ finder method can also be called by providing a pagesize and a pageindex. With these parameters navigate by pages through a search result. See the following example: 

    String serachTerm="(imixs*)";
    // return only the first 10 documents 
    result=documentService.find(serachTerm,10,0);

Note that the pageindex starts with 0. 

###Sorting

Per default the search result is sorted by the lucene internal score of each document returned by the index. To sort the documents by a specific attribute a sortItem and a sort direction can be given:

     String serachTerm="(imixs*)";
    // return only the first 10 documents 
    // sort by $created descending
    result=documentService.find(serachTerm,10,0,'$created',true);
 
### Count Documents of a search result 
The method count() can be used to compute the max count of a  specific serach term.  The method expects the same search term as for the find() method but returns only the count of documents. The method counts only ItemCollections which are accessible by the CallerPrincipal.

 
## The Access Control List of a Document
Additional the _DocumentService_ allows to restrict the read- and write access for a document by providing a [ACL](.acl.html). The items '$readaccess' and '$writeaccess' can be added into a document to restrict the access. The items can provide a list of UserIds or Roles. 

	  @EJB
	  org.imixs.workflow.jee.ejb.DocumentService documentService;
	  //...
	
	  ItemCollection myDocument=new ItemCollection;
	  myDocument.replaceItemValue("type","product");
	  myDocument.replaceItemValue("name","coffee");
	  myDocument.replaceItemValue("weight",new Integer(500));
	  
	  // restrict read access to 'bob'
	  myDocument.replaceItemValue("$readaccess","bob");
	  // restrict write access to 'anna'
	  myDocument.replaceItemValue("$readaccess","anna");
	  	
	  // save ItemCollection
	  myDocument=documentService.save(myDocument);

For further details read the [section ACL](./acl.html).

__Note:__ There is no need to set the Read- and Writeaccess programmatic because the ACL of a _workitem_ can be managed model definition in a transparent way.  
 
  