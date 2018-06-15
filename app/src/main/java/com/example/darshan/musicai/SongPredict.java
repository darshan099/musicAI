package com.example.darshan.musicai;

public class SongPredict {
    public float avg_pop,avg_hiphop,avg_edm,avg_bollywood,avg_others;
    public float avg_total;
    public int[] entergenre(int[] genre,int genre_temp)
    {
        for(int i=0;i<4;i++)
        {
            genre[i]=genre[i+1];
        }
        genre[4]=genre_temp;
        return genre;
    }
    public void generaterating(int[] genre,float[] rating)
    {
        float sum_pop=(float) 0,sum_hiphop=(float) 0,sum_edm=(float) 0,sum_bollywood=(float) 0,sum_others=(float) 0;
        int count_pop=0,count_hiphop=0,count_edm=0,count_bollywood=0,count_others=0;

        for(int i=0;i<5;i++)
        {
            if(genre[i]==1)
            {
                sum_pop=sum_pop+genre[i];
                count_pop++;
            }
            else if(genre[i]==2)
            {
                sum_hiphop=sum_hiphop+genre[i];
                count_hiphop++;
            }
            else if(genre[i]==3)
            {
                sum_edm=sum_edm+genre[i];
                count_edm++;
            }
            else if(genre[i]==4)
            {
                sum_bollywood=sum_bollywood+genre[i];
                count_bollywood++;
            }
            else if(genre[i]==5)
            {
                sum_others=sum_others+genre[i];
                count_others++;
            }
        }
        if(count_pop==0)
        {
            avg_pop=0;
        }
        else
        {
            avg_pop=sum_pop/count_pop;
        }
        if(count_hiphop==0)
        {
            avg_hiphop=0;
        }
        else
        {
            avg_hiphop=sum_hiphop/count_hiphop;
        }
        if(count_edm==0)
        {
            avg_edm=0;
        }
        else
        {
            avg_edm=sum_edm/count_edm;
        }
        if(count_bollywood==0)
        {
            avg_bollywood=0;
        }
        else
        {
            avg_bollywood=sum_bollywood/count_bollywood;
        }
        if(count_others==0)
        {
            avg_others=0;
        }
        else
        {
            avg_others=sum_others/count_others;
        }
        avg_total=(avg_pop+avg_hiphop+avg_others+avg_bollywood+avg_edm)/5;


    }
    public float[] enterrating(float[] rating,float rating_temp)
    {
        for(int i=0;i<4;i++)
        {
            rating[i]=rating[i+1];
        }
        rating[4]=rating_temp;
        return rating;
    }


    public String ratingresult(int result) {
        if(result==1)
        {
            if(avg_total<avg_pop)
            {
                return "you like pop";
            }
            else
            {
                System.out.println(avg_pop+" "+avg_total);
                return "you dont like pop";

            }
        }
        else if(result==2)
        {
            if(avg_total<avg_hiphop)
            {
                return "you like hip-hop";
            }
            else
            {
                System.out.println(avg_hiphop+" "+avg_total);
                return "you dont like hip-hop";
            }
        }
        else if(result==3)
        {
            if(avg_total<avg_edm)
            {
                return "you like edm";
            }
            else
            {
                System.out.println(avg_edm+" "+avg_total);
                return "you dont like edm";
            }
        }
        else if(result==4)
        {
            if(avg_total<avg_bollywood)
            {
                return "you like bollywood";
            }
            else
            {
                System.out.println(avg_bollywood+" "+avg_total);
                return "you dont like bollywood";
            }
        }
        else if(result==5)
        {
            if(avg_total<avg_others)
            {
                return "you like others";
            }
            else
            {
                System.out.println(avg_others+" "+avg_total);
                return "you dont like others";

            }

        }
        else
        {
            return "null";
        }
    }
}

