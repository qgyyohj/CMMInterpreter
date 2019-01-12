# CMMInterpreter
一个cmm语言的解释器<br>
cmm语言的关键词包括:if,else,while,int,real,read,write<br>
  &emsp;特殊符号有:, . + - * / == <> >= <= // /* */ ( ) [ ] { } ;<br>
  &emsp;输入的文件为txt格式的文本文件<br>
功能包含词法分析，语法分析，解释执行<br>
~<b>词法分析</b>：将输入的txt文本文件解析为词法单元Token并返回<br>
~<b>语法分析</b>：根据LL(1)文法，采用递归下降子程序法，将输入的词法单元流组织成语法树<br>
~<b>语义分析</b>：根据上一步得到的语法树，采用递归下降子程序法，采用符号表，将程序解释执行.<br>
<br>
<br>
拓展：<br>
  &emsp;JavaCC的使用：<br>
    &emsp;&emsp;该步骤需要在Eclipse中安装JavaCC插件，安装步骤：帮助->Eclipse Market->搜索JavaCC->点击install->安装完成后重启
