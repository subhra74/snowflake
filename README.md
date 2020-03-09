# Snowflake ![Java CI](https://github.com/subhra74/snowflake/workflows/Java%20CI/badge.svg?branch=master)
Easy and fun way to work with remote servers over SSH.




Snowflake is a graphical SSH client. It has a enhanced SFTP file browser, SSH terminal emulator, remote resource/process manager, server disk space analyzer, remote text editor, huge remote log viewer and lots of other helpful tools, which makes it easy to work with remote servers. Snowflake provides functionality similar to web based control panels but, it works over SSH from local computer, hence no installation required on server. It runs on Linux and Windows.
Snowflake has been tested with serveral Linux and UNIX servers, like Ubuntu server, CentOS, RHEL, OpenSUSE, FreeBSD, OpenBSD, NetBSD and HP-UX.



[![IMAGE ALT TEXT](https://raw.githubusercontent.com/subhra74/snowflake-screenshots/master/Capture32.PNG)](https://youtu.be/G2qHZ2NodeM "View on YouTube")

<h3>Intended audience</h3>
<p>The application is targeted mainly towards web/backend developers who often deploy/debug their code on remote servers and not overly fond of complex terminal based commands. It could also be useful for sysadmins as well who manages lots of remote servers manually.
</p>

<p>
  <a href="https://dev.to/subhra74/how-to-make-you-life-easier-on-remote-linux-servers-ssh-g7m">
    This article explains some more cases
  </a>
</p>

<h3>How it works</h3>
<div>
  <img src="https://github.com/subhra74/snowflake-screenshots/raw/master/arch-overview2.png">
</div>

<h2>Download:</h2>

<table>
  <tr>
    <th>Versions</th>
    <th>Windows</th>
    <th>Ubuntu/Mint/Debian</th>
    <th>Linux</th>
    <th>MacOS</th>
    <th>Other</th>
  </tr>
  <tr>
    <td>
      <a href="https://github.com/subhra74/snowflake/releases/tag/v1.0.4">v1.0.4</a>
    </td>
    <td>
      <a href="https://github.com/subhra74/snowflake/releases/download/v1.0.4/snowflake.msi">MSI installer</a>
    </td>
    <td>
      <a href="https://github.com/subhra74/snowflake/releases/download/v1.0.4/snowflake-1.0.4-setup-amd64.deb">DEB installer</a>
    </td>
    <td>
      <a href="https://github.com/subhra74/snowflake/releases/download/v1.0.4/snowflake-1.0.4-setup-amd64.bin">Generic installer (64 bit)</a>
    </td>
    <td>
      TBD
    </td>
    <td>
      <a href="https://github.com/subhra74/snowflake/releases/download/v1.0.4/snowflake.jar">Portable JAR (Java 13)</a>
    </td>
  </tr>
  <tr>
    <td>
      <a href="https://github.com/subhra74/snowflake/releases/tag/v1.0.3">v1.0.3</a>
    </td>
    <td>
      <a href="https://github.com/subhra74/snowflake/releases/download/v1.0.3/snowflake.msi">MSI installer</a>
    </td>
    <td>
      <a href="https://github.com/subhra74/snowflake/releases/download/v1.0.3/snowflake_1.0-3.deb">DEB installer</a>
    </td>
    <td>
      <a href="https://github.com/subhra74/snowflake/releases/download/v1.0.3/snowflake-1.0.3-setup-amd64.tar.xz">Generic installer (64 bit)</a>
    </td>
    <td>
      TBD
    </td>
    <td>
      <a href="https://github.com/subhra74/snowflake/releases/download/v1.0.3/snowflake.jar">JAR (Java 11)</a>
    </td>
  </tr>
</table>

<h2>Building from source:</h2>
<pre> This is a standard maven project. If you have configured Java and Maven use: 
 <b>mvn clean install</b> to build the project.
 The jar will be created in target directory
 </pre>

<h2>Features:</h2>

<ul>
  <li><a href="#a1">Simple graphical interface for common file operations<a></li>
  <li><a href="#a2">Built in text editor with syntax highlighting and support for sudo<a></li>
  <li><a href="#a3">Simply view and search huge log/text files in a jiffy<a></li>
  <li><a href="#a4">Fast powerful file and content search, powered by find command<a></li>
  <li><a href="#a5">Built in terminal and command snippet<a></li>
  <li><a href="#a6">Fully equiped task manager<a></li>
  <li><a href="#a7">Built in graphical disk space analyzer<a></li>
  <li><a href="#a8">Linux specific tools<a></li>
  <li><a href="#a9">Manage SSH keys easily<a></li>
  <li><a href="#a10">Network tools<a></li>
</ul>

<h4 id="a1">Simple graphical interface for common file operations</h4>
<p>The app is designed to provide a simple graphical interface which allow common activities like moving files on server, renaming, cut, copy, paste, archiving, executing scripts, checking free space, calculating directory size, changing permissions, etc, in simple and efficient way. Though file browsing is based on SFTP, the app uses shell commands whenever posssible to perform operations efficiently. For example deleting a directory having huge number of files and sub directories can take a while using SFTP, but with simple rm command it's much faster. Also the app will prompt to user and can run sudo if priviledged operation needs to be performed. No switching to terminal is needed to invoke sudo. Moving files between servers is also supported with simple drag and drop.</p>

<div>
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/file-browser/1.PNG" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/file-browser/2.png" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/file-browser/3.png" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/file-browser/4.png" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/file-browser/5.PNG" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/file-browser/6.png" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/file-browser/7.PNG" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/file-browser/8.png" width="700">
</div>

<h4 id="a2">Built in text editor with syntax highlighting and support for sudo</h4>
<p>Built in text editor comes in handy when dev or admin needs to modify some files. The editor can invoke sudo and prompt for passwords as needed. This could be very helpfull for modifying global configuration files (like /etc/profile, etc.) from editor without using vi or other terminal based editors.</p>

<div>
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/text-editor/9.PNG" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/text-editor/10.png" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/text-editor/11.png" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/text-editor/12.png" width="700">
</div>

<h4 id="a3">Simply view and search huge log/text files in a jiffy</h4>
<p>The built in log viewer can show huge log files, up to several terabytes, in a very efficient manner. There is no need for downloading the whole file for view or search, thus skipping the pain of waiting for a long time to download the file, or using terminal based tools. The log viewer presents a paginated view of the file, which loads in much less time.</p>

<div>
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/log-viewer/13.PNG" width="700">
</div>

<h4 id="a4">Fast powerful file and content search, powered by find command</h4>
<p>Powerful search functionality, which allows users to find files by name, type, modification date and can also look inside compressed archives. For example it's now very easy to find all the files created in a date range.</p>

<div>
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/search/14.PNG" width="700">
</div>

<h4 id="a5">Built in terminal and command snippet</h4>
<p>With built in terminal, all command line operations can be performed. The terminal is also integrated with the file browser page, so users can open terminal from specific directory or execute scripts in terminal from file browser itself with a click of mouse. Also you can create snippets of your most used commands and execute them with a few clicks without typing again and again.</p>

<div>
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/terminal/15.PNG" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/terminal/16.PNG" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/terminal/17.PNG" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/terminal/18.PNG" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/terminal/19.png" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/terminal/20.PNG" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/terminal/31.PNG" width="700">
</div>

<h4 id="a6">Fully equipped task manager</h4>
<p>Monitor resource usage (CPU, RAM, swap) and view/manage processes from a familiar GUI. It is equipped with search and kill process functionaliy, and also with a option to kill processes with sudo. It's very easy to check which process is using most CPU or memory and view the full command line of the process.</p>

<div>
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/system-monitor/21.PNG" width="700">
   <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/system-monitor/22.png" width="700">
</div>

<h4 id="a7">Built in graphical disk space analyzer</h4>
<p>A friendly GUI which allows users to find out what is eating up diskspace. Any of the mounted partitions or directories can be analyzed.</p>

<div>
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/disk-analyzer/23.PNG" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/disk-analyzer/24.PNG" width="700">
</div>

<h4 id="a8">Linux specific tools</h4>
<p>Few handy tools which can make devs or admins life easier like getting information about the system and distro, starting and stopping systemd services and finding which process is listening on which port.</p>

<div>
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/linux-tools/25.PNG" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/linux-tools/26.PNG" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/linux-tools/27.PNG" width="700">
</div>

<h4 id="a9">Manage SSH keys easily</h4>
<p>Simple and handy UI for creating and managing local and remote SSH keys. Also it supports managing authorized keys from GUI.</p>

<div>
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/ssh-keys/28.PNG" width="700">
</div>

<h4 id="a10">Network tools</h4>
<p>Graphical interface for ping, port checking, traceroute and DNS lookup.</p>

<div>
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/network-tools/29.PNG" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/network-tools/30.PNG" width="700">
</div>



<h2>Documentation:</h2>

<p>
  <a href="https://github.com/subhra74/snowflake/wiki">
    https://github.com/subhra74/snowflake/wiki
  </a>
</p>
