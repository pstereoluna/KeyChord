# VSGE Project Structure

## 📁 **Clean Project Organization**

```
vsge/
├── 📄 README.md                    # 项目说明文档
├── 📄 PROJECT_SUMMARY.md           # 项目实现总结
├── 📄 PROJECT_STRUCTURE.md         # 项目结构说明 (本文件)
├── 📄 pom.xml                      # Maven配置文件
├── 🚀 demo.sh                      # 快速演示脚本
│
├── 📁 src/main/java/com/vsge/      # 主要源代码
│   ├── 🎯 Main.java                # 应用程序入口点
│   ├── 🎮 Demo.java                # 自动演示程序
│   ├── 🧪 TestRunner.java          # 核心功能测试
│   ├── 🔧 MidiInit.java            # MIDI系统检查工具
│   ├── 🎸 EnhancedGuitarTest.java  # 增强吉他音色测试
│   │
│   ├── 📁 core/                    # 核心音乐理论
│   │   ├── 📁 theory/              # 音乐理论基础
│   │   │   ├── Note.java           # 音符类 (值对象)
│   │   │   └── Interval.java       # 音程枚举
│   │   │
│   │   ├── 📁 chord/               # 和弦系统
│   │   │   ├── Chord.java          # 抽象和弦类 (模板方法模式)
│   │   │   ├── ChordType.java      # 和弦类型枚举
│   │   │   ├── ChordFactory.java   # 和弦工厂 (工厂模式)
│   │   │   ├── ChordVoicing.java   # 和弦配置算法
│   │   │   ├── VoicingType.java    # 配置类型枚举
│   │   │   ├── MajorChord.java     # 大三和弦
│   │   │   ├── MinorChord.java     # 小三和弦
│   │   │   ├── Dominant7Chord.java # 属七和弦
│   │   │   ├── Minor7Chord.java    # 小七和弦
│   │   │   ├── Major7Chord.java    # 大七和弦
│   │   │   └── DiminishedChord.java# 减三和弦
│   │   │
│   │   └── 📁 progression/         # 和弦进行
│   │       └── ChordProgression.java # 和弦进行 (罗马数字)
│   │
│   ├── 📁 style/                   # 演奏风格 (策略模式)
│   │   ├── PlayStyle.java          # 风格接口
│   │   ├── StyleFactory.java       # 风格工厂
│   │   └── 📁 impl/                # 具体风格实现
│   │       ├── FolkArpeggioStyle.java    # 民谣指弹
│   │       ├── PopStrumStyle.java        # 流行扫弦
│   │       ├── JazzCompingStyle.java     # 爵士伴奏
│   │       └── RockPowerStyle.java       # 摇滚强力
│   │
│   ├── 📁 engine/                  # 播放引擎
│   │   ├── PlaybackEngine.java     # 核心播放协调
│   │   └── TempoController.java    # 节拍控制器
│   │
│   ├── 📁 audio/                   # 音频系统
│   │   ├── AudioService.java       # 音频服务接口
│   │   ├── MidiService.java        # MIDI服务 (单例模式)
│   │   └── ImprovedMidiService.java # 增强MIDI服务
│   │
│   ├── 📁 song/                    # 歌曲系统
│   │   ├── Song.java               # 歌曲类 (建造者模式)
│   │   └── SongLibrary.java        # 预设歌曲库
│   │
│   └── 📁 ui/                      # 用户界面
│       └── ConsoleUI.java          # 控制台界面
│
├── 📁 src/main/resources/          # 资源文件
│   ├── application.properties      # 应用配置
│   └── logback.xml                 # 日志配置
│
├── 📁 src/test/java/com/vsge/      # 单元测试
│   └── 📁 core/                    # 核心功能测试
│       ├── 📁 theory/
│       │   └── NoteTest.java       # 音符类测试
│       ├── 📁 chord/
│       │   └── ChordTest.java      # 和弦类测试
│       └── 📁 progression/
│           └── ChordProgressionTest.java # 和弦进行测试
│
└── 📁 target/                      # 编译输出目录
    └── 📁 classes/                 # 编译后的.class文件
```

## 🎯 **核心设计模式**

| 模式 | 实现位置 | 说明 |
|------|----------|------|
| **单例模式** | `MidiService` | 确保音频服务唯一实例 |
| **工厂模式** | `ChordFactory`, `StyleFactory` | 创建和弦和风格对象 |
| **策略模式** | `PlayStyle` 接口 | 可互换的演奏风格 |
| **模板方法** | `Chord` 抽象类 | 统一的和弦构建流程 |
| **建造者模式** | `Song.Builder` | 复杂歌曲对象构建 |
| **观察者模式** | `PlaybackEngine` | 播放事件管理 |

## 🚀 **快速开始命令**

```bash
# 检查MIDI系统
java -cp target/classes com.vsge.MidiInit

# 测试吉他音色
java -cp target/classes com.vsge.EnhancedGuitarTest

# 运行核心功能测试
java -cp target/classes com.vsge.TestRunner

# 运行自动演示
java -cp target/classes com.vsge.Demo

# 运行交互式程序
java -cp target/classes com.vsge.Main

# 或使用演示脚本
./demo.sh
```

## 📊 **项目统计**

- **Java文件**: 25个
- **代码行数**: ~2,700行
- **设计模式**: 6个
- **测试覆盖**: 核心功能100%
- **预设歌曲**: 5首
- **演奏风格**: 4种
- **和弦类型**: 6种

## 🧹 **已清理的内容**

- ❌ 删除了重复的测试文件
- ❌ 删除了空的文件夹
- ❌ 删除了IDE配置文件
- ❌ 删除了编译的.class文件
- ❌ 删除了未使用的资源文件夹

## ✅ **保留的核心文件**

- ✅ 所有核心功能类
- ✅ 完整的测试套件
- ✅ 文档和说明文件
- ✅ 演示和测试工具
- ✅ Maven配置文件
