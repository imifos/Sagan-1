/*
* Sagan-1 Robot Simulator
* -----------------------
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* A copy of the GNU General Public License can be found here:
* http://www.gnu.org/licenses/.
*
* Author:
* Tasha CARL, 2011, http://lucubratory.eu / http://sagan-project.eu
*/
package pro.carl.edu.sagan1.logic;

/**
 * Contains the ABOUT and CREDITS text.
 * 
 * @since 0.0
 */
abstract public class About {
      
    /**
     * ABOUT and CREDITS string.
     */
    public final static String ABOUT=
            
            "<html><b><h1>"+Versions.APP_TITLE+", Version "+Versions.APP_VERSION+
            "</h1><br/>Developed by Tasha CARL, <a href=\"http://carl.pro\">http://carl.pro</a>, Mail: tasha"+(char)('B'-2)+"carl"+(char)('0'-2)+"pro<br/><br/>"+
            "Project web page: <a href=\"http://sagan-project.eu\">http://sagan-project.eu</a><br/><br/>"+
            // -------------------------------
            "The program is released as free software (F/OSS) under the GNU GPL v3 license (<a href=\"http://www.gnu.org/licenses/gpl-faq.html\">"+
            "http://www.gnu.org/licenses/gpl-faq.html</a>). This means "+
            "you can use and distribute the binary version as you like.<b>However, you use this software at your own risk. By using it, you "+
            "agree that the result of this utilisation falls under your own responsability. The developers cannot hold liable for any damage that "+
            "might happen to your hardware, software or your robot.</b><br/><br/>"+
            "THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,"+
            "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER "+
            "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN "+
            "THE SOFTWARE.<br/><br/>"+
            // -------------------------------
            "<hr/><b>Copyright Notice in case ESA images are used for robot missions, the following terms of use applies:</b><br/><br/>"+
            "The ESA Portal Multimedia Gallery contains images and videos used throughout the ESA Portal. The images are offered in the Gallery "+
            "in the highest resolution available. Most images have been released publicly from ESA. You may use ESA images or videos for educational "+
            "or informational purposes. The publicly released ESA images may be reproduced without fee, on the following conditions:<br/><ul>"+
            "<li>Credit ESA as the source of the images (this is hereby done).</li>"+
            "<li>ESA images may not be used to state or imply the endorsement by ESA or any ESA employee of a commercial product, process or service, "+
            "or used in any other manner that might mislead.</li>"+
            "<li>If an image includes an identifiable person, using that image for commercial purposes may infringe that person‘s right of privacy, and "+
            "separate permission should be obtained from the individual.</li></ul>"+
            "Some images contained in this Gallery have come from other sources, and this is indicated in the Copyright notice. For re-use of non-ESA "+
            "images contact the designated authority.<br/><br/>"+
            "Sagan One is open-source, free and non-commercial, educational software and should be distributed as it. By this, we take advantange of the "+
            "permissions granted by the ESA license.<br/><br/>"+
            "ESA URL: <a href=\"http://www.esa.int/esa-mmg/mmgdownload.pl\">http://www.esa.int/esa-mmg/mmgdownload.pl</a>, Mail: multimedia"+(char)('B'-2)+"esa"+(char)('0'-2)+"int"+
            // -------------------------------
            "<br/><br/><hr/><b>Cross-compilation to RP6 HEX files and upload:</b><br/><br/>"+
            "The compilation of your program into a RP6 HEX program is integrated into Sagan One"+
            " when you install the WinAVR cross-compiler packages. As a constraint, this works currently only under Windows. "+
            "You can find the latest release here: <a href=\"http://winavr.sourceforge.net\">http://winavr.sourceforge.net/</a>. "+
            "<br/><br/>To upload the HEX file into the RP6, please use the classic RP6Loader "+
            "(<a href=\"http://www.arexx.com/rp6/html/en/software.htm\">http://www.arexx.com/rp6/html/en/software.htm</a>) or the newer "+
            "version RobotLoader (<a href=\"http://www.arexx.com/rp6/downloads/RobotLoader_20100712.zip\">http://www.arexx.com/rp6/downloads/RobotLoader_20100712.zip</a>)."+
            //
            "<br/><hr/><b>Cross-compilation to Lego NXT Mindstorm files and upload:</b><br/><br/>"+
            "The compilation and the upload of your program into a NXT program is integrated into Sagan One when running under Windows."+
            "<br/><br/>Sagan One uses the 'Not eXactly C' (NXC) cross compiler written by John C. Hansen. There is also a Linux and Mac version that "+
            "we will integrate later, if possible. Please don't hesitate to buy John's great book about Lego NXT Power-Programing.<br><br/>"+
            "The latest version of the NXC/NBC packages can be found here: <a href=\"http://bricxcc.sourceforge.net/nbc/\">http://bricxcc.sourceforge.net/nbc/</a>.<br><br/>"+
            "<b>Please note that the NXT support is currently in a beta phase. We are still trying to find the most precise operation mode."+
            // -------------------------------
            "<br/><br/><hr/><b>Included libraries:</b><br/><br/>This distribution contains the RP6 libraries written by Dominik S. Herwald, released under GNU GPL v2"+
            " and distributed as part of the RP6 robot kit. These libraries are free and open-source software as well.<br/>"+
            "<br/>'Not eXactly C' (NXC) cross compiler written by John C. Hansen."+
            // -------------------------------
            "<br/><br/><hr/><b>Installer:</b><br/><br/>The installer has been build with BitRock InstallBuilder Professional, "+
            " provided as free open-source project license by BitRock S.L. Thank you! <br/>"+
            "<a href=\"http://www.bitrock.com\">http://www.bitrock.com</a><br><br/>"+
            "<br/><br/>";
}
