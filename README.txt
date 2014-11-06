It uses Pearson's coefficient to calculate the similarity between sample user and other users. It then uses these co-efficients to predict the rating that the sample user will give for a unrated movie.

The trainRatings.txt has the trainingData needed for the Machine Learning algorithm to learn the the co-efficients. 
Once learning completes(it takes a couple of minutes as the number of records is 3.5 million in the txt file), the program predicts the ratings for movies given in testRatings.txt file. It reports accuracy on the testRatings.txt in percentage.

Can run on?
You can run this file on Linux server or in any IDE for Java.

Developed using?
JRE7 and Netbeans.

Compile command for Linux:
javac CollaborativeFiltering.java

Run command for linux:
java CollaborativeFiltering <trainRatings.txt> <testRatings.txt>

Keep the trainRatings.txt and testRatings.txt in the same folder as the .java file and Specify their name as arguments during runtime
OR
Specify the entire path and name of the file as arguments during runtime.