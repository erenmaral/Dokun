#!/usr/bin/env python3
"""i18n/<dil>.json sozluklerini index.html icindeki /*I18N-BEGIN*/ ... /*I18N-END*/ blogune gomer. Kullanim: python3 i18n/embed.py"""
import json, os, re
root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
data = {}
for l in ['en', 'es', 'de', 'fr', 'pt']:
    p = os.path.join(root, 'i18n', l + '.json')
    if os.path.exists(p): data[l] = json.load(open(p, encoding='utf-8'))
blob = json.dumps(data, ensure_ascii=False, separators=(',', ':')).replace('</', '<\\/')
f = os.path.join(root, 'index.html'); s = open(f, encoding='utf-8').read()
s2 = re.sub(r'/\*I18N-BEGIN\*/.*?/\*I18N-END\*/', lambda m: '/*I18N-BEGIN*/' + blob + '/*I18N-END*/', s, flags=re.S)
open(f, 'w', encoding='utf-8').write(s2)
print('gomuldu:', {k: len(v) for k, v in data.items()})
