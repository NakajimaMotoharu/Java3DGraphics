# Java3DGraphics

【このプログラムは開発中です】  
Javaの標準ライブラリのみで3Dグラフィックスの描画を行うプログラムを作りました。  
  
処理の基本単位は面で、OBJファイルやWLDファイル(独自のファイル形式)を読み込むことができます。  
任意角形の面の各辺を線分で表現し、ワイヤーフレーム形式で描画します。  
これらを塗りつぶしてサーフェスで描画するメソッドを開発予定です。  

各メソッドの簡単な動作説明は、各メソッドの上部に記載してあります。  
動作環境によっては非常に重たくなる可能性があります。  
また、ほとんどの処理がCPU(シングルスレッド)依存となっております。  
このプロジェクトで独自に定義した.wld形式のファイルはサンプルを同封してあります。

用意していただくファイルはOBJファイル、WLDファイル、設定ファイルです。  
それぞれサンプルが用意してあります。  
OBJファイルはユタのティーポットを用意しました。  
引用URL: https://graphics.stanford.edu/courses/cs148-10-summer/as3/code/as3/teapot.obj  
WLDファイルはそのティーポットを囲むような箱を用意してあります。  
設定ファイルは初期設定などを記したファイルで、ユタのティーポットに合わせてサンプルを用意しました。  
用意したOBJファイルやWLDファイルの読み込みは、Mainプログラムをテンプレートに従って改変してください。

操作はキーボードとマウスを使うことができます。  
移動はWASDで、視点移動はIJKL or マウスです。  
Eキーを押すことでマウスでの視点操作を切り替えられます(トグル式)。  
プログラムの終了については、いくつかの手段を用意してあります。  
Qキーを押すことでプログラムを終了できます。  
サブウィンドウの表示を許可していた場合、EXITボタンを押すことでも終了できます。  
また、当然ウィンドウの右上にあるウィンドウを閉じるボタンを押しても問題ありません。  


# UPDATE LOG
20231205 アップロード
