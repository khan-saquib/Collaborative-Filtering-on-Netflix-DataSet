/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;



/**
 *
 * @author Saquib
 */
public class CollaborativeFiltering {
   
    Map<Integer, ArrayList<Movie>> userData;
    Map<Integer, ArrayList<User>> movieData;
    Map<Integer, HashMap<Integer,Double>> weights;
    Map<Integer, Double> userVoteMean;
    
    private class Movie {

        int movieID;
        double rating;

        public Movie(int movieID, double rating) {
            this.movieID = movieID;
            this.rating = rating;
        }
        
        @Override
        public boolean equals(Object obj) {
            if(obj==null)
                return false;
            else if(obj.getClass()!=this.getClass())
                return false;
            else
                return movieID == ((Movie)obj).movieID; //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    
    private class User {
        
        int userID;
        double rating;

        public User(int userID, double rating) {
            this.userID = userID;
            this.rating = rating;
        }
    }
    
    
    public CollaborativeFiltering()
    {
        userData = new HashMap<>();
        movieData = new HashMap<>();
        weights = new HashMap<>();
        userVoteMean = new HashMap<>();
    }
    
    /**
     * Reads from the file and populates userData and movieData maps
     */
    public void readFromFile(File file) throws FileNotFoundException
    {
        Scanner scanner = new Scanner(file);
        List<String> words;
        words = new ArrayList<>();
        String temp;
        String[] data;
        
        int userID, userIDPrev;
        int movieID;
        double rating;
        
        //Get the first line
        temp = scanner.nextLine();
        data = temp.split("[,]");
        userIDPrev = Integer.parseInt(data[0]);
        movieID = Integer.parseInt(data[1]);
        rating = Double.parseDouble(data[2]);
        ArrayList<Movie> movies; 
        ArrayList<User> users;
        //Add the first record into the userData and movieData
        userData.put(userIDPrev, movies = new ArrayList<>());
        movies.add(new Movie(movieID,rating));
        movieData.put(userIDPrev, users = new ArrayList<>());
        users.add(new User(userIDPrev,rating));
        
        
        while (scanner.hasNextLine()) 
        {
            //Get the three values from the line
            temp = scanner.nextLine();
            data = temp.split("[,]");
            userID = Integer.parseInt(data[0]);
            movieID = Integer.parseInt(data[1]);
            rating = Double.parseDouble(data[2]);
            
            //Populate the userData
            if(userID!=userIDPrev)
            {
                userData.put(userID, movies = new ArrayList<>());
                movies.add(new Movie(movieID, rating));
            }
            else
            {
                movies.add(new Movie(movieID,rating));
            }
            
            //Populate the movieData
            users = movieData.get(movieID);
            if(users == null)
            {
                movieData.put(movieID, users = new ArrayList<>());
            }
            users.add(new User(userID,rating));

            //Update the userIDPrev for the next iteration
            userIDPrev = userID;
        }
    }
    
    
    
    public void populateVoteMeanForAllUser()
    {
        List<Movie> movies = new ArrayList<>();
        Set<Integer> usersAll = userData.keySet();
        double voteCount,sum, votesMean;
        for(int userID: usersAll)
        {
           movies = userData.get(userID);
           voteCount = movies.size();
           sum = 0;
           for(Movie movie:movies)
           {
               sum = sum + movie.rating;
           }
           votesMean = sum/voteCount;
           userVoteMean.put(userID, votesMean);
//           System.out.println(userID + " " + votesMean);
        }
    }
    
    public void calculateWeights()
    {
        List<Integer> userIDs = new ArrayList<>(userData.keySet());
        Collections.sort(userIDs);
        HashMap<Integer,Double> temp;
        double weight,sum1,sum1Sq,sum2,sum2Sq,temp_double,user1MeanVote,user2MeanVote;
        List<Movie> movieList1, movieList2;
        int index, userID1, userID2;
        HashMap<Integer,Double> mapOfMovies1,mapOfMovies2;
        
        for(int i=0;i<userIDs.size()-1;i++)
        {
            userID1 = userIDs.get(i);
            weights.put(userID1, temp = new HashMap<>());
            movieList1 = userData.get(userID1);
            user1MeanVote = userVoteMean.get(userIDs.get(i));
            mapOfMovies1 = new HashMap<>();
            
            for(Movie m: movieList1)
            {
                mapOfMovies1.put(m.movieID, m.rating);
            }
            
            for(int j=i+1;j<userIDs.size();j++)
            {
                userID2 = userIDs.get(j);
                movieList2 = userData.get(userID2);
                user2MeanVote = userVoteMean.get(userID2);
                sum1 = 0.0;
                sum1Sq = 0.0;
                sum2 = 0.0;
                sum2Sq = 0.0;
                mapOfMovies2 = new HashMap<>();
                for(Movie m: movieList2)
                {
                    mapOfMovies2.put(m.movieID, m.rating);
                }
                
                //Search using HashMap
                for(Integer key:mapOfMovies1.keySet())
                {
                    Double rating = mapOfMovies2.get(key);
                    if(rating!=null)
                    {
                        temp_double = (double)mapOfMovies1.get(key) - user1MeanVote;
                        sum1 += temp_double;
                        sum1Sq += (temp_double*temp_double);

                        temp_double = (double)rating - user2MeanVote;
                        sum2 += temp_double;
                        sum2Sq += (temp_double*temp_double);  
                    } 
                }
                
                /***********Linear running time to search*************/
                
//                for(Movie movie: movieList1)
//                {
//                    index = -1;
                    
//                    for(int m=0; m<movieList2.size();m++)
//                    {
//                        if(movie.movieID == movieList2.get(m).movieID)
//                       {
//                           index = m;
//                           break;
//                       }
//                    }
//                    if(index!=-1)
//                    {
////                        System.out.println("found matching movies");
//                        temp_double = (double)movie.rating - user1MeanVote;
//                        sum1 += temp_double;
//                        sum1Sq += (temp_double*temp_double);
//                        
//                        temp_double = (double)movieList2.get(index).rating - user2MeanVote;
//                        sum2 += temp_double;
//                        sum2Sq += (temp_double*temp_double);
//                    }
//                }
                
                if(sum1Sq == 0.0 || sum2Sq == 0.0)
                    weight = 0;
                else
                {
                    weight = sum1*sum2/(Math.sqrt(sum1Sq*sum2Sq));
//                    System.out.println(userID2 + " " + weight);
                }
                temp.put(userID2, weight);
//                System.out.println(userID1+" "+userID2+" "+weight);
            }
//         System.out.println(i+" of "+userIDs.size());   
        }
        
        
    }
    
    public double PredictRating(int userID, int movieID)
    {
        double sum = 0.0, sumWeight = 0.0, weight, temp;
        double vaMean = userVoteMean.get(userID);
        List<User> users = movieData.get(movieID);
        
        for(User user: users)
        {
            if(userID<user.userID)
            {
                weight = weights.get(userID).get(user.userID);
                temp = user.rating - userVoteMean.get(user.userID);
                sum += temp*weight;
                sumWeight += Math.abs(weight);
            }
            else if(userID > user.userID)
            {
                weight = weights.get(user.userID).get(userID);
                temp = user.rating - userVoteMean.get(user.userID);
                sum += (temp*weight);
                sumWeight += Math.abs(weight);
            }
        }
        
        double prediction = vaMean + sum/sumWeight;
        return prediction;
    }
    
    
    public static void main(String[] args) throws FileNotFoundException
    {
        File trainFile = new File(args[0]);
        CollaborativeFiltering cf = new CollaborativeFiltering();
        cf.readFromFile(trainFile);
        cf.populateVoteMeanForAllUser();
        cf.calculateWeights();
//        for(Integer userID : cf.weights.get(8).keySet())
//            System.out.println("8," + userID + "," + cf.weights.get(8).get(userID));
        
        
        System.out.println("Error on the Test Data:");
        
        String[] data;
        File testFile = new File(args[1]);
        Scanner scanner = new Scanner(testFile);
        String temp;
        int userID,movieID, count = 0;
        double rating, predictedRating, meanError = 0, meanSquareError = 0;
        
        while(scanner.hasNextLine())
        {
            count++;
            temp = scanner.nextLine();
            data = temp.split("[,]");
            userID = Integer.parseInt(data[0]);
            movieID = Integer.parseInt(data[1]);
            rating = Double.parseDouble(data[2]);
            predictedRating = cf.PredictRating(userID, movieID);
//            System.out.println(rating + " " + predictedRating);
            meanError += Math.abs(predictedRating - rating);
            meanSquareError += Math.pow(predictedRating - rating, 2);
        }
        System.out.println("Error :" + meanError/count);
        System.out.println("Error :" + Math.sqrt(meanSquareError/count));
    }
}
