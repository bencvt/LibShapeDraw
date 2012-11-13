#!/usr/bin/env python
"""
Preprocess the one source code file that directly references (obfuscated)
Minecraft classes. This allows us to use human-readable identifiers everywhere.

The mappings in this script will change for new Minecraft versions. Credit to
MCP (Minecraft Coder Pack) for maintaining its obfuscation maps, containing
thousands of entries. We only use a small subset here!
"""
import os.path
NAME_SRC = 'mod_LibShapeDraw.java'
INPUT_SRC = os.path.join('..', 'java', 'net', 'minecraft', 'src', NAME_SRC)
OUTPUT_SRC = os.path.join('..', '..', '..', '..', 'main', 'src', 'main', 'java',
    NAME_SRC)
OBFUSCATION_MAP = dict(reversed(x.split()) for x in """
    ap  Entity.dimension
    t   Entity.posX
    u   Entity.posY
    v   Entity.posZ
    q   Entity.prevPosX
    r   Entity.prevPosY
    s   Entity.prevPosZ
    ayk EntityClientPlayerMP
    S   GameSettings.hideGUI
    b   GuiIngame.getChatGUI
    a   GuiNewChat.printChatMessage
    r   Minecraft.currentScreen
    y   Minecraft.gameSettings
    x   Minecraft.getMinecraft
    v   Minecraft.ingameGUI
    w   Minecraft.skipRenderWorld
    g   Minecraft.thePlayer
    e   Minecraft.theWorld
    axz NetClientHandler
    kh  Profiler
    b   Profiler.endSection
    c   Profiler.endStartSection
    a   Profiler.startSection
    aro RenderHelper
    b   RenderHelper.enableStandardItemLighting
    bao Tessellator
    a   Tessellator.addVertex
    a   Tessellator.draw
    a   Tessellator.instance
    b   Tessellator.startDrawing
    asr Timer
    c   Timer.renderPartialTicks
""".strip().split('\n'))
HEADER = """
// THIS SOURCE FILE WAS AUTOMATICALLY GENERATED. DO NOT MANUALLY EDIT.
// Edit projects/dev/src/main/java/net/minecraft/src/mod_LibShapeDraw.java
// and then run the projects/dev/src/main/python/obfuscate.py script.
""".lstrip()

def main():
    output = open(OUTPUT_SRC, 'w')
    output.write(HEADER)
    count = 0
    repl = None
    for line in open(INPUT_SRC):
        if line.strip() == 'package net.minecraft.src;':
            # Filter out. Obfuscated classes live in the root package.
            count += 1
            continue
        if line.strip().startswith('// obf:'):
            # Marker listing the obfuscateable names that will be present in
            # the next line.
            repl = {}
            for mapkey in line.strip()[7:].split(','):
                text = mapkey = mapkey.strip()
                if '.' in text:
                    _, _, text = text.partition('.')
                repl[text] = OBFUSCATION_MAP[mapkey]
        elif repl:
            # This is the line following a marker. Make the specified
            # replacements, then forget the repl map for subsequent lines.
            for text in repl:
                line = line.replace(text, repl[text])
            count += 1
            repl = None
        output.write(line)
    output.close()
    print('input:  ' + os.path.abspath(INPUT_SRC))
    print('output: ' + os.path.abspath(OUTPUT_SRC))
    print(str(count) + ' lines adjusted')

if __name__ == '__main__':
    os.chdir(os.path.dirname(os.path.realpath(__file__)))
    main()
