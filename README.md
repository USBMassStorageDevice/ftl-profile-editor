ftl-profile-editor
==================

[Vhati](https://github.com/Vhati/ftl-profile-editor) finally got some documentation and binaries up, so get the [more official builds from their site](http://sourceforge.net/projects/ftleditor/)

Personal Notes
--------------

**FTL Profile Editor** is some sort of save editor Java application thing for [FTL: Faster Than Light](http://store.steampowered.com/app/212680) that I came across after browsing the FTL Game forums.

Except the posted version didn't work, and there isn't much documentation. But hey, maven project means easy to build: [Downloads Page](https://github.com/yaozornation/ftl-profile-editor/downloads). 

From what I figured out so far, this seems to be the workflow:

1.  Run it. You probably need  [Java](http://www.java.com)
2.  Find the location of FTL's resource.dat, which would be `SteamLibrary\SteamApps\common\FTL Faster Than Light\resources` (or maybe `Steam\steamapps\common` on older installs)
3.  Find the location of your prof.sav, which in Windows: `(My) Documents\My Games\FasterThanLight`
4.  Push all of the buttons
5.  Back up your existing prof.sav somewhere
6.  Hit Save, then overwrite `prof.sav`
7.  Hope your game still runs
