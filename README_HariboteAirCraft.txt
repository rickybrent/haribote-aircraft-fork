HariboteAirCraft (はりぼてエアクラフト)
====================

## 概要

ブロックをはりぼて化して動かすことのできる「羅針盤ブロック」を追加します。
はりぼてはキー操作で操縦することができます。飛行船や飛行機などを作るのにちょうどいいかな？


## 前提

- Minecraft 1.4.7
- ModLoader 1.4.7
- YMTLib_147v4


## 導入

mods に zip のまま放り込んでください。
導入前にバックアップを取るのも忘れずに。".minecraft" 自体をバックアップするのがお手軽です。


## 利用条件

この MOD は Apache License(ver2.0) の下で配布されます。

    http://www.apache.org/licenses/LICENSE-2.0

- この MOD を使用したことにより発生したいかなる結果についても、製作者は一切の責任を負いません。
- この MOD は変更の有無にかかわらず再頒布が可能です。Apache License を確認してください。

この MOD または派生成果物は、それが minecraft を前提としている場合に、
minecraft 自体の利用条件に縛られることに注意してください。
利用条件の詳細は minecraft の利用規約を確認してください。


## レシピ

### 羅針盤ブロック　←　黒曜石３個、ダイヤ２個、金ブロック１個、コンパス１個

    　●　
    ◇□◇
    ■■■


## 使い方

- 羅針盤ブロックを設置し、右クリックするとON/OFFされます。
- 移動はテンキーで行います。マインクラフトの設定画面より他のキーを割り当てることもできます。
- 起動するとモブは強制的に座ります。足元を右クリックすることで起立/着席できます。


## Tips

- 移動するときは座りましょう。座らないと振り落とされます。
- 飛行船は地面などに当たると一旦止まります。
- 飛行船は一定時間入力がないと、その場で止まります。その時間はConfig[moveKeepTime]から設定できます。(デッドマン装置)
- 移動中のブロックは見た目だけです。ブロックとしての機能はありません。
- 一部の非対応ブロックは、移動中の見た目が羊毛ブロックになります。羅針盤をOFFにすると元のブロックに戻ります。
- 強制停止するときには、はりぼて終了キーを押してください。


## 操作説明(初期値)

    はりぼて前進    = テンキー1    (MaxSpeed = 4)
    はりぼて後退    = テンキー3    (MaxSpeed = 1)
    はりぼて上昇    = テンキー8    (MaxSpeed = 4)
    はりぼて下降    = テンキー2    (MaxSpeed = 4)
    はりぼて右旋回  = テンキー9    (MaxSpeed = 4)
    はりぼて左旋回  = テンキー7    (MaxSpeed = 4)
    はりぼて右移動  = テンキー6    (MaxSpeed = 3)
    はりぼて左移動  = テンキー4    (MaxSpeed = 3)
    はりぼて停止    = テンキー5    (停止中に停止するとブロックグリッドに整列)
    はりぼて終了    = テンキー/    (その場で強制的に再ブロック化)

キーは押し続けたり連打しなくても、１回押すだけでその方向に動き出します。
キーを重ねて入力するとその方向へスピードアップします。逆方向のキーでスピードダウンします。


## Config (mod_HariboteAirCraft.cfg)

    IdPyxis=209                             # 羅針盤ブロックのID
    blockLimit=2000                         # 一度に動かせる表面ブロックの最大数
    craftBodySize=-1                        # 飛行船の当たり判定の大きさ(-1で見た目通り)
    blockTarget=                            # 移動可能なブロックID(指定の無いときにはデフォルトセットが使用されます)
    blockAppend=                            # 追加で移動可能なブロックID(blockTargetに追加で指定します)
    blockIgnore=2, 3, 8, 9, 10, 11, 12, 13, 31, 32, 37, 38, 78, 87, 121   # 移動されないブロックID(初期値としてワールドの表層ブロックを指定しています)
    moveKeepTime=60                         # 無入力時に移動し続ける時間


## Copyright 2013 Yamato

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


## History

- 147v6: 移動スピードなど調整しました。緊急停止ボタンを付けました。ピストン、ビーコン、醸造台などが動かせるようになりました。
- 147v5: 151系の更新に伴って見つかったバグを修正しました
- 147v4: Java6 で動かない部分を修正しました。YMTLib_147v4 以降のバージョンが必要です。
- 147v3: 描画系の処理を見直し、軽量化を図りました。チェスト、かまど、ディスペンサー、金床などが動かせるようになりました。
- 147v2: 旋回に伴ってプレイヤーも方向を変えるようになりました。ドア、ベッド、植木鉢などが動かせるようになりました。
- 147v1: 初版


## Appendix

- DefaultMoveableSet

         1      Stone                       石
         2      Grass                       草
         3      Dirt                        土
         4      Cobblestone                 丸石
         5      Wood Plank                  木材
         6      Sapling                     木の苗
         8      Water                       水
         9      Stationary Water            水
         10     Lava                        溶岩
         11     Stationary Lava             溶岩
         12     Sand                        砂
         13     Gravel                      砂利
         14     Gold Ore                    金鉱石
         15     Iron Ore                    鉄鉱石
         16     Coal Ore                    石炭
         17     Wood                        原木
         18     Leaves                      木の葉
         19     Sponge                      スポンジ
         20     Glass                       ガラス
         21     Lapis Lazuli Ore            ラピスラズリ鉱石
         22     Lapis Lazuli Block          ラピスラズリブロック
         23     Dispenser                   ディスペンサー
         24     Sandstone                   砂岩
         25     NoteBlock                   音符ブロック
         26     Bed                         ベッド
         27     Powered Rail                パワードレール
         28     Detector Rail               ディテクターレール
         29     Sticky Piston               粘着ピストン
         30     Web                         クモの巣
         31     Tall Grass                  背の高い草
         32     Dead Shrub                  枯れ木
         33     Piston                      ピストン
         34     Piston Head                 ピストンアーム
         35     Wool                        羊毛
         37     Dandelion                   花
         38     Rose                        バラ
         39     Brown Mushroom              茶きのこ
         40     Red Mushroom                赤きのこ
         41     Gold Block                  金ブロック
         42     Iron Block                  鉄ブロック
         43     Double Stone Slab           石ハーフブロック(２段重ね)
         44     Stone Slab                  石ハーフブロック
         45     Brick                       レンガブロック
         46     TNT                         TNT
         47     Bookshelf                   本棚
         48     Mossy Cobblestone           苔むした丸石
         49     Obsidian                    黒曜石
         50     Torch                       たいまつ
         51     Fire                        火
         53     Wood Stairs                 木の階段
         54     Chest                       チェスト
         55     Redstone Wire               レッドストーンワイヤー
         56     Diamond Ore                 ダイヤ鉱石
         57     Diamond Block               ダイヤブロック
         58     Workbench                   作業台
         59     Wheat                       小麦
         60     Soil                        農地
         61     OvenIdle                    かまど
         62     OvenActive                  燃えているかまど
         64     Wooden Door                 木製のドア
         65     Ladder                      はしご
         66     Rails                       レール
         67     Cobblestone Stairs          丸石の階段
         69     Lever                       レバー
         70     Stone Pressure Plate        石の感圧板
         71     Iron Door                   鉄製のドア
         72     Wooden Pressure Plate       木の感圧板
         73     Redstone Ore                レッドストーン鉱石
         74     Glowing Redstone Ore        レッドストーン鉱石
         75     Redstone Torch (off)        レッドストーントーチ
         76     Redstone Torch (on)         レッドストーントーチ
         77     Stone Button                石ボタン
         78     Snow                        雪
         79     Ice                         氷
         80     Snow Block                  雪ブロック
         81     Cactus                      サボテン
         82     Clay                        粘土ブロック
         83     Sugar Cane                  サトウキビ
         85     Fence                       フェンス
         86     Pumpkin                     かぼちゃ
         87     Netherrack                  ネザーラック
         88     Soul Sand                   ソウルサンド
         89     Glowstone                   グロウストーン
         90     Portal                      ポータル
         91     Jack-O-Lantern              かぼちゃランタン
         92     Cake                        ケーキ
         93     Redstone Repeater (off)     レッドストーンリピーター
         94     Redstone Repeater (on)      レッドストーンリピーター
         96     TrapDoor                    トラップドア
         97     Stone (Silverfish)          石(シルバーフィッシュ)
         98     Stone Brick                 石レンガブロック
         99     Red Mushroom Cap            赤キノコブロック
         100    Brown Mushroom Cap          茶キノコブロック
         101    Iron Bars                   鉄格子
         102    Glass Pane                  板ガラス
         103    Melon Block                 スイカ
         104    Pumpkin Stem                かぼちゃの苗
         105    Melon Stem                  スイカの苗
         106    Vines                       ツタ
         107    Fence Gate                  フェンスゲート
         108    Brick Stairs                レンガの階段
         109    Stone Brick Stairs          石レンガの階段
         110    Mycelium                    菌糸ブロック
         111    Lily Pad                    蓮の葉
         112    Nether Brick                ネザーレンガブロック
         113    Nether Brick Fence          ネザーレンガフェンス
         114    Nether Brick Stairs         ネザーレンガの階段
         115    Nether Wart                 ネザーいぼ
         116    Enchantment Table           エンチャント台
         117    Brewing Stand               醸造台
         118    Cauldron                    大釜
         121    End Stone                   エンドストーン
         122    Dragon Egg                  ドラゴンエッグ
         123    Redstone Lamp (inactive)    レッドストーンランプ
         124    Redstone Lamp (active)      レッドストーンランプ
         125    Double Wood Slab            木ハーフブロック(２段重ね)
         126    Wood Slab                   木ハーフブロック
         127    Cocoa Plant                 カカオの実
         128    Sandstone Stairs            砂岩の階段
         129    Emerald Ore                 エメラルド鉱石
         130    EnderChest                  エンダーチェスト
         131    Tripwire Hook               トリップワイヤーフック
         132    Tripwire                    トリップワイヤー
         133    Emerald Block               エメラルドブロック
         134    Spruce Wood Stairs          木の階段(松)
         135    Birch Wood Stairs           木の階段(白樺)
         136    Jungle Wood Stairs          木の階段(ジャングル)
         137    Command Block               コマンドブロック
         138    Beacon                      ビーコン
         139    Cobblestone Wall            丸石フェンス
         140    Flower Pot                  植木鉢
         141    Carrots                     ニンジン
         142    Potatoes                    じゃがいも
         143    Wooden Button               木ボタン
         145    Anvil                       金床
