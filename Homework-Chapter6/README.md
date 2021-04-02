## Homework-Chapter6

### 作业要求

1. sharedpreference的使用，首页标题每次打开+1，比如：todo，todo1，todo2，todo3

2. 数据库操作：长按编辑修改某一条todolist的内容。

### 我实现的功能

完成了所有任务

1. `SharedPreferences`的使用, 实现方式是在`MainActivity`中自定义`onResume`方法, 每次执行`onResume`方法就在`SharedPreferences`中存储的数字`number`中加一(`number`就是标题的todo后面的数字)

2. 长按编辑修改的实现方式是对`RecyclerView`里面的条目`NoteViewHolder`设置`onLongClickListener`, 长按后打开一个`AlertDialog`进行操作

### 应用实际操作录屏

见`homework-chapter6演示视频.webm`