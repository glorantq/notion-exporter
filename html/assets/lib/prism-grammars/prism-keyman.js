Prism.languages.keyman = {
	'comment': /\bc\s.*/i,
	'function': /\[\s*(?:(?:ALT|CAPS|CTRL|LALT|LCTRL|NCAPS|RALT|RCTRL|SHIFT)\s+)*(?:[TKU]_[\w?]+|".+?"|'.+?')\s*\]/i,  // virtual key
	'string': /("|').*?\1/,
	'bold': [   // header statements, system stores and variable system stores
		/&(?:baselayout|bitmap|capsalwaysoff|capsononly|copyright|ethnologuecode|hotkey|includecodes|keyboardversion|kmw_embedcss|kmw_embedjs|kmw_helpfile|kmw_helptext|kmw_rtl|language|layer|layoutfile|message|mnemoniclayout|name|oldcharposmatching|platform|shiftfreescaps|targets|version|visualkeyboard|windowslanguages)\b/i,
		/\b(?:bitmap|bitmaps|caps always off|caps on only|copyright|hotkey|language|layout|message|name|shift frees caps|version)\b/i
	],
	'keyword': /\b(?:any|baselayout|beep|call|context|deadkey|dk|if|index|layer|notany|nul|outs|platform|reset|return|save|set|store|use)\b/i,  // rule keywords
	'atrule': /\b(?:ansi|begin|group|match|nomatch|unicode|using keys)\b/i,   // structural keywords
	'number': /\b(?:U\+[\dA-F]+|d\d+|x[\da-f]+|\d+)\b/i, // U+####, x###, d### characters and numbers
	'operator': /[+>\\,()]/,
	'tag': /\$(?:keyman|keymanonly|keymanweb|kmfl|weaver):/i   // prefixes
};
