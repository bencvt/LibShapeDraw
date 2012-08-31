# About Trident

Trident is pretty much the only mature open-source Java animation library out
there that isn't part of a much larger framework.

It's also inactive as of August 2010, which actually isn't as bad as it sounds
because the project was already fairly mature and robust. And hey, it's
open-source; nothing's stopping anyone from running with a fork should the need
arise.

Limited documentation, full source code, and releases are available in several
places: the [Trident author's blog](http://www.pushing-pixels.org/category/trident),
the [kenai.com project](http://kenai.com/projects/trident/pages/Home), and
the [GitHub snapshot](https://github.com/kirillcool/trident).

# Usage in LibShapeDraw

Rather than reinvent the wheel, LibShapeDraw includes Trident to assist client
code in animating its shapes. The integration is intentionally loose; client
code using LibShapeDraw is *not* obliged to use any part of Trident.

See `src/test/java/mod_LSDDemoTrident*.java` for sample usage.

For ease of distribution, LibShapeDraw includes a built-in version of Trident.
It has been modified in several ways:

 +  Repackaged as `libshapedraw.animation.trident`.

 +  Removed Android/Swing/SWT subpackages as they're not relevant to a Minecraft
    environment.

 +  Removed support for loading `META-INF/trident-plugin.properties`, as this is
    typically nuked for Minecraft client modding purposes. Core property
    interpolators are now hard-coded.

 +  Added support for fluent property setters, e.g.:

        // POJO that doesn't support method chaining ("fluent interfacing").
        public class MyClass {
            // Trident can access this property, e.g.:
            // new Timeline(new MyClass()).addPropertyToInterpolate("x", 0.0, 1.0);
            private double x;
            public void setX(double x) { this.x = x; }
        }
        // x's setter supports method chaining
        public class MyFluentClass {
            // Without the added support, Trident can't interpolate this property.
            private double x;
            public MyFluentClass setX(double x) { this.x = x; return this; }
        }

# Licenses

See `LICENSE.txt` for LibShapeDraw's license. (TL;DR: it's the MIT license.)

Trident's license is compatible:

    Copyright (c) 2005-2010 Trident Kirill Grouchnikov. All Rights Reserved.
    
    Redistribution and use in source and binary forms, with or without 
    modification, are permitted provided that the following conditions are met:
    
     o Redistributions of source code must retain the above copyright notice, 
       this list of conditions and the following disclaimer. 
        
     o Redistributions in binary form must reproduce the above copyright notice, 
       this list of conditions and the following disclaimer in the documentation 
       and/or other materials provided with the distribution. 
        
     o Neither the name of Trident Kirill Grouchnikov nor the names of 
       its contributors may be used to endorse or promote products derived 
       from this software without specific prior written permission. 
        
    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
    THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
    PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
    CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
    EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
    PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
    OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
    WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
    OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
    EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

----
Trident uses the TimingFramework internally, which is similarly licensed:

    Copyright (c) 2006, Sun Microsystems, Inc
    All rights reserved.
    
    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions
    are met:
    
     Redistributions of source code must retain the above copyright
        notice, this list of conditions and the following disclaimer.
     Redistributions in binary form must reproduce the above
        copyright notice, this list of conditions and the following 
        disclaimer in the documentation and/or other materials provided 
        with the distribution.
     Neither the name of the TimingFramework project nor the names of its
        contributors may be used to endorse or promote products derived 
        from this software without specific prior written permission.
    
    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
    "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
    LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
    A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
    OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
    SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
    LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
    DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
    THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
    OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
