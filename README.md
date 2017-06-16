# gdx-components
GUI components for an open source, OpenGl based game-development framework libGDX.

## Background info

I started work on this some time ago in order to create some GUI with libGDX, and have been modifying and expanding it since, depending on my needs or enthusiasm. I don't really have any grand suggestions as to what you can do with it, but I decided to publish it in case someone find it useful, or manage to carve something useful out of it.

## How to use this

To use this library, you definitely need the libGDX library in your project, and most likely some understanding of it. You can acquire the libGdx library, along with some samples and tutorials, from its website here: https://libgdx.badlogicgames.com/

As to this library itself, you should be able to just put it in your project to make it work. Hopefully, I will add some samples how to build something out of them in the future, but currently, you are pretty much in the dark. Just note that there isn't anything fancy like XML layouts, you just have to go into Java and start calling constructors.

## Work in progress

* Any 3D stuff, intended for building 3D user interfaces, is highly experimental and probably not at all useful without substantial work put into it.
* Shader effects are experimental, unoptimized, and more complicated shader effects may very well not work.
* GdxScrollView and GdxPager are currently worked on, so they're likely to have bugs and missing features.
* Testing is limited and bugs are consistently found even in a very old code, so this paragraph kind of applies to the entire project.

## Some legal info

This library is provided as is for any kind of use, with fairly limited chances of support. Some parts are known to be incomplete and/or not working as intended. The author does not take responsibility for any resulting damage.

## Thanks to

Thanks to libGDX community for making cross platform OpenGL easy and accessible for those of us who don't speak assembler.
