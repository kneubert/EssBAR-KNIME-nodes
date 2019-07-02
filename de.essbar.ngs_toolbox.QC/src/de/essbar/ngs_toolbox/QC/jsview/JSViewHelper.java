/**
 * The MIT License
 *
 * Copyright (c) 2010-2012 www.myjeeva.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE. 
 * 
 */

package de.essbar.ngs_toolbox.QC.jsview;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
//import org.apache.commons.lang3.StringUtils;

import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;


/**
 * Helper class to parse HTML files.
 * 
 * @author neubert
 */
public class JSViewHelper {

    /**
     * Utility class should have private c'tor.
     */
    private JSViewHelper() {
    }

    
    /**
     * Reads a file into a String
     * 
     * @param file
     *            The file to read.
     * @return The string containing the first maxLines lines.
     * @throws IOException
     *             if the file does not exist or cannot be opened or read.
     */
    public static String readFile(final File file)
            throws IOException {
    	
    	int maxLines = 100000;
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        StringBuffer sb = new StringBuffer();
        try {

            String line = "";
            int cnt = 0;
            Date date = new Date(file.lastModified());
            Format formatter = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss");
            String s = formatter.format(date);
            while ((line = br.readLine()) != null) {
               
            	if (!line.isEmpty()) {
            		sb.append(line + System.getProperty("line.separator"));
            		//sb.append(line);
            		cnt++;
            	}
                if (cnt > maxLines) {
                    sb.append("######### OUTPUT TRUNCATED #########").append(
                            System.getProperty("line.separator"));
                    break;
                }
            }
        } catch (IOException ex) {
            // close readers
            br.close();
            fr.close();
            // rethrow
            throw ex;
        }

        // close readers
        br.close();
        fr.close();
       
        return sb.toString();
    }    
  
    
    /**
     * Encodes pictures in a html file
     * 
     * @param in_file
     *            The file to read.
     * @return modified file with base64 encoded pictures
     * @throws IOException
     *             if the file does not exist or cannot be opened or read.
     */
   public static File embed_pictures(final File in_file)
		   throws IOException {
	   
	   System.out.println("emped pictures in file " + in_file.getParent().toString() );
	   String testHtml = FileUtils.readFileToString(in_file, Charset.forName("utf-8")); // from commons io
	   Document document = Jsoup.parse(testHtml);
	   Elements images = document.getElementsByTag("img"); 
	   System.out.println("found " + images.size() + " images in html");
	   String dir = in_file.getParent();
	   
	   for (Element el : images) {
			
			String src = el.getElementsByTag("img").attr("src");
			//System.out.println("with src: " + src);
		    Elements pics_found = document.select("img[src="+src+"]");

			    String src_text = convert_image(src,dir);
			    for (Element p : pics_found) {
			    	p.replaceWith(new Element(Tag.valueOf("img"),"").attr("src", src_text));
			    }
			//System.out.println("replace " + src);
			//System.out.println("by " + convert_image(src, dir));
	
		}

		File file_out = new File(dir + "/qualimapReport_encoded.html");
		BufferedWriter bw = new BufferedWriter(new FileWriter(file_out));
		bw.write(document.toString());
		bw.close();
		
		return file_out;
   }
   
   
   
   /**
    * Encodes pictures and stylesheets in a html file
    * 
    * @param in_file
    *            The file to read.
     * @return modified file with base64 encoded pictures and stylesheet
    * @throws IOException
    *             if the file does not exist or cannot be opened or read.
    */
   	public static File embed_pictures_and_style(final File in_file)
		   throws IOException {
	   
	   System.out.println("emped pictures in file " + in_file.getParent().toString() );
	   String testHtml = FileUtils.readFileToString(in_file, Charset.forName("utf-8")); // from commons io
	 
	   Document document = Jsoup.parse(testHtml);
	   Elements images = document.getElementsByTag("img"); 
	   System.out.println("found " + images.size() + " images in html");
	   String dir = in_file.getParent();
	   
	   for (Element el : images) {
			
			String src = el.getElementsByTag("img").attr("src");
			System.out.println("with src: " + src);
		    Elements pics_found = document.select("img[src="+src+"]");

			String src_text = convert_image(src,dir);
			for (Element p : pics_found) {
				p.replaceWith(new Element(Tag.valueOf("img"),"").attr("src", src_text));
			}
			System.out.println("replace " + src);
			System.out.println("by " + convert_image(src, dir));
	
		}
	   
	    // convert stylesheets by byte64 encoding
		Elements links = document.select("link[type=\"text/css\"]"); // a with href
		
		System.out.println("found " + links.size() + " links in html.");
		
		for (Element el : links) {
		
			String src = el.getElementsByTag("link").attr("href");
			//System.out.println("with src: " + src);	
			String src_text = convert_stylesheet(src,dir);
			Elements css_found = document.select("link[href="+src+"]");
			
			for (Element p : css_found ) {
				p.attr("href", src_text);
			}
			//System.out.println("by " + convert_stylesheet(src, dir));
			
		}

		File file_out = new File(dir + "/qualimapReport_encoded.html");
		BufferedWriter bw = new BufferedWriter(new FileWriter(file_out));
		bw.write(document.toString());
		bw.close();
		
		return file_out;
   	}
   
   	
    /**
     * base64 encoding of a picture
     * 
     * @param image_src
     *            The image file.
     * @param directory
     *            Directory of the image file.
     * @return base64 encoded string for the image
     * @throws IOException
     *             if the image file does not exist or cannot be opened or read.
     */
	public static String convert_image(String image_src, String directory) 
			throws IOException {
		String output = "data:image/png;base64,";
		String imageDataString = "";
		
		try {
			File file = new File(directory + "/" + image_src);
			FileInputStream imageInFile = new FileInputStream(file);
			byte imageData[] = new byte[(int) file.length()];
			imageInFile.read(imageData);
			imageInFile.close();
			// covert image byte array into Base64 String
			imageDataString = encode(imageData);
			
		
		} catch (FileNotFoundException e) {
			System.out.println("Image not found" + e);
		} catch (IOException ioe) {
			System.out.println("Exception while reading the Image " + ioe);
		}

		String result = output + imageDataString;
		return result;
	}

	
    /**
     * base64 encoding of a picture
     * 
     * @param src
     *            The css file.
     * @param directory
     *            Directory of the css file.
     * @return base64 encoded string for the image
     * @throws IOException
     *             if the css file does not exist or cannot be opened or read.
     */
	public static String convert_stylesheet(String src, String directory) {
		
	//	String output = "<img src='data:image/png;base64,";
		String output = "data:text/css;base64,";
		String cssDataString = "";

		// replace pictures in stylesheet by base64 encoding
		try {
			File file = new File(directory + "/" + src);
			File dir = file.getParentFile();
			//System.out.println("try to mask url in css");
			String css_string = FileUtils.readFileToString(file,Charset.forName("utf-8")); // from commons io
			Pattern p = Pattern.compile("(url)\\((\\w+.\\w+)\\)");
			Matcher m = p.matcher(css_string);
			
			// replace by byte64 encoded string "data: image/png; base64, iVBORw0 ... Y%2Fsfiv02O7iVu1LunAAAAAElFTkSuQmCC"
			String replacement = "";
			
			StringBuffer string_result = new StringBuffer();
			while (m.find()) {
				String png_src = m.group(2);
				replacement = convert_image(png_src, dir.toString());
				m.appendReplacement(string_result, m.group(1) + "(" + replacement + ")");
				
			}
			m.appendTail(string_result);
			System.out.println(string_result);
			
			File file_mod = new File(directory + "/" + src + ".tmp");
			BufferedWriter bw = new BufferedWriter(new FileWriter(file_mod));
			bw.write(string_result.toString());
			bw.close();
			
			FileInputStream cssInFile = new FileInputStream(file_mod);		
			byte cssData[] = new byte[(int) file_mod.length()];
			cssInFile.read(cssData);	
			cssInFile.close();
			// covert css byte array into Base64 String
			cssDataString = encode(cssData);
			
		
		} catch (FileNotFoundException e) {
			System.out.println("Image not found" + e);
		} catch (IOException ioe) {
			System.out.println("Exception while reading the file " + ioe);
		}
			String result = output + cssDataString;
			return result;
		}
	
	
	/**
	 * Encodes the byte array into base64 string
	 *
	 * @param ByteArray - byte array
	 * @return String a {@link java.lang.String}
	 */
	public static String encode(byte[] ByteArray) {
 	
		//return Base64.encodeBase64URLSafeString(imageByteArray);
		return Base64.encodeBase64String(ByteArray);
	}

}

