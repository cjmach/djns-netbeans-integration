# djns-netbeans-integration

A module suite that integrates [DJ Native Swing](https://github.com/Chrriis/DJ-Native-Swing) 
components in [Apache NetBeans](https://github.com/apache/netbeans).

## Features

- Allows to set DJ Native Swing Web Browser as the default NetBeans browser. This 
web browser is displayed inside the main NetBeans window and it's also used to 
view javadocs, HTML files, etc.
- Adds a Preview view to the NetBeans HTML editor.
- Adds [NativeDialogBuilder](https://github.com/cjmach/djns-netbeans-integration/blob/main/djns/src/pt/cjmach/netbeans/djns/dialog/NativeDialogBuilder.java) 
class that supports the integration of native dialogs in NetBeans.

## Installation

We use Github Pages to publish the module suite's update site whenever a new 
release is created. The following lists the steps to install the suite:
1. Open NetBeans
2. Click on the menu Tools / Plugins
3. Select the "Settings" tab
4. Click on the "Add" button to add a new update site with the following values:
  - **Name**: DJ Native Swing Update Site
  - **URL**: https://cjmach.github.io/djns-netbeans-integration/updates.xml
5. Select the "Available Plugins" and click on the "Check for Newest" button. 
6. You must check exactly two modules:
  - **The main module**: DJ Native Swing Integration
  - **The native module**: DJ Native Swing Integration - [Your OS/Arch]
7. Click on the "Install" button and follow the instructions of the install wizard.

## Configuration

The following lists the steps to configure DJ Native Swing Web Browser as the 
default NetBeans browser:

1. Click on the menu Tools / Options
2. Select the "General" tab
3. Select "DJ Native Swing Browser" from the Web Browser drop-down list
4. Click "OK"

The web browser can be opened by clicking on the menu Window / Web / Web Browser. 
NetBeans IDE will also use it to show the Javadocs, etc. 
