To get generated code and to run it in Eclipse:

1. In sbt shell, type compile,press enter, then click Run on top left

2. Open your browser, type in localhost:9000/next

   2.1 next is for Cycle

   2.2 strong is for StronglyConnectd

   2.3 comp is for Connected

3. In the browser, click compute. It can take couple minutes. Then click on Git, you should see this:
   git clone -b variation_0 http://localhost:9000/next/next.git

4. In terminal, you can cd into a directory, paste what you get in last step,
   all the generated code will be in the directory.

5. In Eclipse, Create a project. Right click on the project, open Properties.
   Click Java Build Path, then link source to the directory where you put the
   code in last step.

6. Now you should be able to see the code in Eclipse, you can also run the
   Junit test cases.



