import java.math.BigInteger;

import java.nio.file.Files;
import java.nio.file.Paths;

class Interpreter{
	public static void main(final String[]a)throws Exception{
		final byte[] data=Files.readAllBytes(Paths.get("Output.ufbb"));
		final int[] data2=new int[data.length];
		final StringBuilder builder=new StringBuilder();
		for(int i=0;i<data.length;i++)
			data2[i]=data[i]&0xff;
		for(final int i:data2)
			builder.append(manPadding(Integer.toBinaryString(i), 8));
		System.out.println(builder.toString());
	}
	private static String manPadding(final String str, final int i){
    final StringBuilder reverse=new StringBuilder(str).reverse();
    while(reverse.length()<i)
      reverse.append("0");
    while(reverse.length()>i)
      reverse.delete(0, 1);
    return reverse.reverse().toString();
  }
}
