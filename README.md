# MMXCODE

mmxcode == media mirror and transcode

If you have a pile of media (a directory tree) in one set of formats, and you want to make an exact copy 
of that pile, but in other formats, then this may be useful. 

Why would you want this? Usually in cases where you have high-resolution files (like FLAC) but need to 
downgrade them so you can point something like iTunes and an iPod at them, and do that repeatedly. This 
is a command-line tool, mostly for nerds.

Features:
- can do a transcode or copy action for any file it finds in the source but not the destination, using configurable rules
- can run on any platform, and with any encoders and decoders, as long as you configure them properly
- handles deletes in the destination as well as timestamp-based updates (so for example as you update tags in the master, the mirror gets updated)

Ok, then:

- Download the latest build or the src (mavenized java).
- Read the Usage instructions
- Get in touch with me, this is MID-DEVELOPMENT.

# Usage Guide

Rules, Tools, and Formats
=========================

Mmxcode relies at its core on formats, tools, and rules, each of which are configured with JSON format configuration files int he /conf directory.

  * Formats are file formats, which have a name and an extension.
  * Tools are installed encoders and decoders, which operate on the various Formats
  * Rules are the things that specify how files are moved to the mirror

When using the tool, you must first make sure the tools are configured as you need.  You can run the testformats command to see which tools are set up:

  mmxcode testformats

You only need to have tools configured for the formats you are going to oeprate on in your rules.  If for example all you are doing is transcoding flac to mp3, you'd just need flac and lame.  Edit the tools.json file appropriately for your system.

Once the tools are in place, you can set up your Rules by editing rules.json.  The rules should be self-explanatory, the actions available to you are COPY or TRANSCODE.

By configuring the rules.json file, you can create any kind of mirroring/transcoding behavior you'd like.
 

Running the Tool
================

Let's say you have the directory /Users/will/music/all, and you want to create a directory /Users/will/music/mirrored full of copied or transcoded files.

Run the tool like this:
 
  mmxcode.sh /Users/will/music/all /Users/will/music/mirrored

You can run the tool with no arguments to get help.


Configuration Details
=====================

  * platform names are "win" and "osx"
  

Other Common Tasks
==================

TBD operating on single dirs









