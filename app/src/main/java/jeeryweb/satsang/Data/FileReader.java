package jeeryweb.satsang.Data;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import jeeryweb.satsang.R;

/**
 * Created by Debo#Paul on 3/28/2018.
 */

public class FileReader {

    List districtsInState , prayerTimeInState;
    private final String Tag = "SearchActivity";

    public void read1(Context c){
        InputStream inputStream = c.getResources().openRawResource(R.raw.district);

        List<String> resultList = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;

            while ((csvLine = reader.readLine()) != null) {
                resultList.add(csvLine);
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }

        districtsInState =  resultList;

        //Log.e("Data", (String)districtsInState.get(0));
    }

    public void read2(Context c){
        InputStream inputStream = c.getResources().openRawResource(R.raw.prayer_timing);


        List<String> resultList = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;

            while ((csvLine = reader.readLine()) != null) {
                resultList.add(csvLine);
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }

        prayerTimeInState =  resultList;
        //Log.e("Data", (String)prayerTimeInState.get(0));
    }

    public String queryWithDistrict(String d){

        if(d.equals(null))
            return "NA";

        d = d.toLowerCase();

        int po=districtsInState.size();
        Log.e("entry in db= ",String.valueOf(po));
        String previous = null;

        for(int i=0;i<po;i++){
            String row= String.valueOf(districtsInState.get(i));

            //Log.e("row", row);

            String p = row.split(",")[0];
            if(p.length()!=0)previous = p;


            if(row.toLowerCase().contains(d)){
                String row_array[] = row.split(",");

                if(row_array[0].length()==0 && previous!=null)
                    return previous;
                //Log.e("StateName", row_array[0]);
                return row_array[0];
            }
        }
        return "NA";
    }

    public String queryWithState(String stateName, int month){

        if(stateName.equals(null) || month<0 || month>12)
            return "NA";

        stateName = stateName.toLowerCase();

        int numberCols = (month-1)*2+1;
        String output;
        for(int it =0;it<prayerTimeInState.size();it++){

            String row= String.valueOf(prayerTimeInState.get(it));


            if(row.toLowerCase().contains(stateName)){
                String row_array[] = row.split(",");

                for(int i=0;i<row_array.length;i++){
                        if(i==numberCols){
                            output =  row_array[i] + "," + row_array[i+1];
                            return output;
                        }
                }
            }

        }
        return "NA";
    }

    public List<String> getAllZones(){

        List<String> res = new ArrayList<String>();

        int po=districtsInState.size();

        for(int i=0;i<po;i++){
            String row= String.valueOf(districtsInState.get(i));

            String p = row.split(",")[0];
            if(p.length()!=0)
                res.add(p);
        }
        Log.e(Tag, "res.lenghth "+res.size());
        return res;
    }

    public List<String> getAllDistrictsOfAZone(String query){

        List<String> res = new ArrayList<String>();
        int po=districtsInState.size();
        int temp_set = 0;
        for(int i=0;i<po;i++){
            String row= String.valueOf(districtsInState.get(i));
            String p = row.split(",")[0];

            if(p.length()!=0){
                if(p.equals(query)){
                    Log.e(Tag, row);

                    String row_arr[] = row.split(",");

                    Log.e(Tag, String.valueOf(row_arr.length));
                    for(int j=0;j<row_arr.length;j++){
                        if(j==0)continue;

                        //hacky fix for strings of the nature "dejradun or " or lumding"
                        if(row_arr[j].charAt(0) == '\"' && row_arr[j].length()==1){
                            continue;
                        }
                        else if(row_arr[j].charAt(0) == '\"'){
                            row_arr[j]  =  row_arr[j].split("\"")[1];
                        }
                        else if(row_arr[j].charAt(row_arr[j].length()-1)=='\"'){
                            row_arr[j]  =  row_arr[j].split("\"")[0];
                        }
                        res.add(row_arr[j]);
                    }
                    Log.e(Tag, "Search for district "+res.size());
                    temp_set = 1;
                    return res;
                }
            }else{
//                if(temp_set==0)continue;
//
//                String row_arr[] = row.split(",");
//
//                Log.e(Tag, String.valueOf(row_arr.length));
//                for(int j=0;j<row_arr.length;j++){
//                    if(j==0)continue;
//
//                    //hacky fix for strings of the nature "dejradun or " or lumding"
//                    if(row_arr[j].charAt(0) == '\"' && row_arr[j].length()==1){
//                        continue;
//                    }
//                    else if(row_arr[j].charAt(0) == '\"'){
//                        row_arr[j]  =  row_arr[j].split("\"")[1];
//                    }
//                    else if(row_arr[j].charAt(row_arr[j].length()-1)=='\"'){
//                        row_arr[j]  =  row_arr[j].split("\"")[0];
//                    }
//
//
//                    res.add(row_arr[j]);
//                }
//                Log.e(Tag, "Search for district "+res.size());
//                return  res;
            }
        }
        return null;
    }


}
