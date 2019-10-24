# Snowflake
Easy and fun way to work with SSH

Snowflake is a graphical SSH client. It has a file browser, terminal emulator, resource/process manager, disk space analyzer, text editor, log viewer and lots of other helpful tools, which makes it easy to work with remote servers. It runs on Linux and Windows.
Snowflake has been tested with Ubuntu server, CentOS, RHEL, OpenSUSE, FreeBSD, OpenBSD, NetBSD and HP-UX

<h2>Download:</h2>

<table>
  <tr>
    <th>Windows</th>
    <th>Ubuntu/Mint/Debian</th>
    <th>Other Linux</th>
    <th>MacOS</th>
    <th>Other</th>
  </tr>
  <tr>
    <td>
      <a href="https://github.com/subhra74/snowflake/releases/download/v1.0.1/snowflake.msi">MSI installer</a>
    </td>
    <td>
      <a href="https://github.com/subhra74/snowflake/releases/download/v1.0.1/snowflake_1.0-1.deb">DEB installer</a>
    </td>
    <td>
      <a href="https://github.com/subhra74/snowflake/releases/download/v1.0.1/snowflake-1.0.1-linux-amd64.tar.xz">Generic installer (64 bit)</a>
    </td>
    <td>
      TBD
    </td>
    <td>
      <a href="https://github.com/subhra74/snowflake/releases/download/v1.0.1/snowflake.jar">JAR (Java 11)</a>
    </td>
  </tr>
</table>


<h2>Features:</h2>

<h4>1. Simple graphical interface for common file operations</h4>
<p>The app is designed to provide a simple graphical interface which allow common activities like moving files on server, renaming, cut, copy, paste, archiving, executing scripts,checking free space, calculating directory size, changing permissions etc in simple and efficient way. Though file browing is based on SFTP, the app uses shell commands whenever posssible to perform operations efficiently. For example deleting a directory having huge number of files and sub directories can take a while using SFTP, but with simple rm command its much faster. Also the app will prompt to user and can run sudo if priviledged operation needs to be performed. No switching to terminal is needed to invoke sudo. Moving files between servers is also supported with simple drag and drop.</p>

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

<h4>2. Built in text editor with syntax highlighting and support for sudo</h4>
<p>Built in text editor comes in handy when dev or admin needs to modify some files. The editor can invoke sudo and prompt for passwords as needed. This could be very helpfull for modifying global configuration files( like /etc/profile etc ) from editor without using vi or other terminal based editors.</p>

<div>
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/text-editor/9.PNG" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/text-editor/10.png" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/text-editor/11.png" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/text-editor/12.png" width="700">
</div>

<h4>3. Simply view and search huge log/text files in a jiffy</h4>
<p>The built in log viewer can show huge log files, upto several terabytes in a very efficient manner. There is no need for downloading the whole file for view or search, thus skipping the pain of waiting for a long time to download the file, or using acrane terminal based tools. The log viewer presents a paginated view of the file, which loads in much less time.</p>

<div>
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/log-viewer/13.PNG" width="700">
</div>

<h4>4. Fast powerful file and content search, powered by find command</h4>
<p>Powerful search functionality, which allows users to find files by name, type, modification date and can also look inside compressed archives. for example its now very easy to find all the files created between a date range dates.</p>

<div>
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/search/14.PNG" width="700">
</div>

<h4>5. Built in terminal and command snippet</h4>
<p>With built in terminal, all command line operations can be performed. The terminal is also integrated with the file browser page, so users can open terminal from specific directory or execute scripts in terminal from file browser itself with a click of mouse. Also You can create snippet of your most used commands and execute them with a few clicks without typing again and again.</p>

<div>
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/terminal/15.PNG" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/terminal/16.PNG" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/terminal/17.PNG" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/terminal/18.PNG" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/terminal/19.png" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/terminal/20.PNG" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/terminal/31.PNG" width="700">
</div>

<h4>6. Fully equiped task manager</h4>
<p>Monitor resource usage (CPU,RAM,SWAP) and view/manage processes from a familier GUI. It is equipped with search and kill process functionaliy, and also with a option to kill processes with sudo. Its very easy to check which process is using most CPU or Memeory and the full command line of the process.</p>

<div>
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/system-monitor/21.PNG" width="700">
   <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/system-monitor/22.png" width="700">
</div>

<h4>7. Built in graphical disk space analyzer</h4>
<p>A friendly GUI which allows users to find out what is eating up the diskspace. Any of the mounted partitions or directories can be analyzed.</p>

<div>
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/disk-analyzer/23.PNG" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/disk-analyzer/24.PNG" width="700">
</div>

<h4>8. Linux specific tools</h4>
<p>Few handy tools which can make dev's or admin's life earier like getting information about the system and distro, starting and stopping systemd services and finding which process is listening on which port.</p>

<div>
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/linux-tools/25.PNG" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/linux-tools/26.PNG" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/linux-tools/27.PNG" width="700">
</div>

<h4>9. Manage SSH keys easily</h4>
<p>Simple and handy UI for creating and managing local and remote SSH keys. Also it supports managing authorized keys from GUI.</p>

<div>
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/ssh-keys/28.PNG" width="700">
</div>

<h4>10. Network tools</h4>
<p>Graphical interface for PING,Port checking, Traceroute and DNS lookup</p>

<div>
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/network-tools/29.PNG" width="700">
  <img src="https://github.com/subhra74/snowflake-screenshots/blob/master/network-tools/30.PNG" width="700">
</div>



<h2>Documentations:</h2>
https://github.com/subhra74/snowflake/wiki


