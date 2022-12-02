/**
 *
 *	Unsafe Four Bit is a compiled-interpreted, dynamically-typed programming language.
 *	Copyright (C) 2022  JumperBot_
 *
 *	This program is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
**/

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

class UFB{
	public static void main(final String[]a)throws Exception{
    final FlagManager flagManager=new FlagManager(a);
    if(flagManager.isFlagActivated('v')){
      //----------------------------------------------------------------------//
      /**TODO: ALWAYS CHANGE SEMANTIC VERSION BEFORE RELEASING.
       * DO NOT Change '1' in "1.*.*".
       * MINOR CHANGES should go in "1.MINOR.*".
       * PATCH CHANGES should go in "1.*.PATCH".
       * MINOR CHANGES should give new commands/major features.
       * PATCH CHANGES should give new flags/performance-boosts/bug-fixes/etc.
      **/
      //----------------------------------------------------------------------//
      final String versionString="v1.5.0";
      System.out.printf(
        "UFB version: %s (master)\n%s\n\n",
        versionString,
        "Flag triggered, continuing anyway..."
      );
      // Inspired By:
      // https://www.javaguides.net/2019/07/java-http-getpost-request-example.html
      System.out.println("Checking if UFB is up-to-date...");
      HttpURLConnection connection=(HttpURLConnection)new URL(
        "https://api.github.com/repos/JumperBot/Unsafe-4-Bit/releases/latest"
      ).openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("User-Agent", "Mozilla/5.0");
      final int responseCode=connection.getResponseCode();
      System.out.printf(
        "%s: %d...\n",
        "GitHub API responded with code",
        responseCode
      );
      if(responseCode==HttpURLConnection.HTTP_OK){
        try(BufferedReader in=new BufferedReader(
          new InputStreamReader(connection.getInputStream())
        )){
          final String inputLine=in.readLine();
          final int versionInd=inputLine.indexOf("\"name\":")+8;
          final String latestVersion=inputLine.substring(
            versionInd,
            inputLine.indexOf(
              ",",
              versionInd
            )-1
          );
          if(!latestVersion.equals(versionString))
            System.out.printf(
              "%s\n\n%s\n%s\n\n%s\n%s\n%s\n\n",
              "UFB on this machine is not up-to-date!",
              "Download the latest .jar file:",
              "https://github.com/JumperBot/Unsafe-4-Bit/raw/master/build/UFB.jar",
              "Or clone the repository and get continous updates:",
              "git clone https://github.com/JumperBot/Unsafe-4-Bit.git",
              "Version check triggered, continuing anyway..."
            );
          else
            System.out.printf(
              "%s\n%s\n\n",
              "UFB on this machine is up-to-date!",
              "Version check triggered, continuing anyway..."
            );
        }
      }else
        System.out.printf(
          "%s\n%s\n\n",
          "Version check failed...",
          "Version check triggered, continuing anyway..."
        );
    }
    if(flagManager.isFlagActivated('h')){
      final String repo="https://github.com/JumperBot/Unsafe-4-Bit";
      final String master="/tree/master/";
      System.out.printf(
        "%s\n%s\n%s%s%s\n%s%s%s\n%s\n%s\n%s\n\n",
        "Need help? Either visit these links:",
        repo,
        repo, master, "src#ufb",
        repo, master, "test#commands",
        "or visit the examples in the 'test' folder...",
        "only if you fully cloned the repository.",
        "Flag triggered, continuing anyway..."
      );
    }
    if(flagManager.isFlagActivated('l'))
      System.out.printf(
        "%s\n%s\n%s\n%s\n%s\n\n",
        "----------------------------------------------------------------------------------\n",
        new StringBuilder("GNU GENERAL PUBLIC LICENSE - Version 3, 29 June 2007\n\n")
        .append("Unsafe Four Bit is a compiled-interpreted, dynamically-typed programming language.\n")
        .append("Copyright (C) 2022  JumperBot_\n")
        .append("\n")
        .append("This program is free software: you can redistribute it and/or modify\n")
        .append("it under the terms of the GNU General Public License as published by\n")
        .append("the Free Software Foundation, either version 3 of the License, or\n")
        .append("(at your option) any later version.\n")
        .append("\n")
        .append("This program is distributed in the hope that it will be useful,\n")
        .append("but WITHOUT ANY WARRANTY; without even the implied warranty of\n")
        .append("MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n")
        .append("GNU General Public License for more details.\n")
        .append("\n")
        .append("You should have received a copy of the GNU General Public License\n")
        .append("along with this program.  If not, see <https://www.gnu.org/licenses/>.\n").toString(),
        "The copy of the license can be viewed at the root of this repository.\n",
        "----------------------------------------------------------------------------------\n",
        "Flag triggered, continuing anyway..."
      );
    final String fileName=flagManager.getFileName();
    if(fileName.length()==0){
      System.out.println("\u001B[91mNo file input found, terminating.\u001B[0m");
      System.exit(1);
    }
    if(!new File(fileName).exists()){
      System.out.println("\u001B[91mFile Provided Does Not Exist...\nTerminating...\u001B[0m");
      System.exit(1);
    }
    if(flagManager.isFlagActivated('c')){
      if(fileName.endsWith(".ufbb")){
        System.out.printf(
          "%s\n%s\n",
          "\u001B[91mCould not compile an already compiled source code.",
          "Remove the compilation flag to run the compiled program.\u001B[0m"
        );
        System.exit(1);
      }
      new UFBC().compile(fileName, !flagManager.isLongFlagActivated("unoptimized"));
      return;
    }
    if(fileName.endsWith(".ufb")){
      System.out.printf(
        "%s\n%s\n",
        "\u001B[91mCould not run uncompiled source code.",
        "Add the compilation flag to compile the program.\u001B[0m"
      );
      System.exit(1);
    }
    new Runner(
      fileName,
      flagManager.isFlagActivated('p'),
      flagManager.isFlagActivated('n'),
      flagManager.isFlagActivated('m'),
      flagManager.isFlagActivated('b')
    ).run();
	}
}