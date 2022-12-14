
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class AllTimeHighh 
{
  	public static class mymapper extends Mapper<LongWritable, Text, Text, DoubleWritable>
  	{
  		public void map(LongWritable key, Text value, Context context)
	      {	    	  
	         try{
	            String[] str = value.toString().split(",");	 
	            double high = Double.parseDouble(str[4]);
	            context.write(new Text(str[1]),new DoubleWritable(high));
	         }
	         catch(Exception e)
	         {
	            System.out.println(e.getMessage());
	         }
	      }

  	}

  public static class myreducer extends Reducer<Text,DoubleWritable,Text,DoubleWritable> 
  {
	  private DoubleWritable result = new DoubleWritable();
	    
	    public void reduce(Text key, Iterable<DoubleWritable> values,Context context) throws IOException, InterruptedException {
	      double max = 0.00;
			
	         for (DoubleWritable val : values)
	         {       	
	        	if(val.get()>max)   
	        	{
	        		max=val.get();
	        	}
	         }
	         
	      result.set(max);		      
	      context.write(key, result);    
	      
	    }
  }

public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "All_time_high ");
    job.setJarByClass(AllTimeHighh.class);
    job.setMapperClass(mymapper.class);
    job.setReducerClass(myreducer.class);
    job.setNumReduceTasks(1);
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(DoubleWritable.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(DoubleWritable.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}



