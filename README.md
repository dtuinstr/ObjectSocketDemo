This is similar to the SocketDemo project you've already seen,
but this project connects the server and client using an 
<tt>InputObjectStream</tt> and an <tt>OutputObjectStream</tt>,
rather than a <tt>StreamReader</tt> and a <tt>StreamWriter</tt>.

This allows us to pass objects back and forth, rather than just
strings (but since <tt>String</tt>s are objects, we can still 
send and receive them). This flexibility comes at the cost of some
additional complexity in coding -- and debugging!

Make changes incrementally, test after each one, and commit often.

To run:
<ol>
<li> Download code into IDEA.</li> 
<li> From IDEA's 'Build' menu, build the project.</li>
<li> Server
   <ol>
   <li> Open terminal/command window</li>
   <li> cd to {project_directory}/out/production/ObjectSocketDemo</li>
   <li> enter 'java ObjectSocketDemo' to see usage message</li>
   <li> enter 'java ObjectSocketDemo server 4466' to start server on port 4466</li>
   <li> you should see a server startup message</li>
   <li> leave window open</li>
   </ol>
   </li>
<li> Client
   <ol>
   <li> Open terminal/command window</li>
   <li> cd to {project_directory}/out/production/ObjectSocketDemo</li>
   <li> enter 'java ObjectSocketDemo' to see usage message</li>
   <li> enter 'java ObjectSocketDemo client localhost 4466' to start client.</li>
   <li> you should see something like
     
        [Server listening. 'Logout' (case insensitive) closes connection.]
        
   <li> type stuff; when you hit Enter it should be echoed back to you</li>
   <li> when you enter a line with just the word 'logout' (case-insensitive), 
        both client and server stop with a message</li>
   </ol>
</ol>
