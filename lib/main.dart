import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:url_launcher/url_launcher.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'URI Scheme Launcher',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        useMaterial3: true,
      ),
      home: const MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key});

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  final TextEditingController _uriController = TextEditingController();
  String _errorMessage = '';

  Future<void> _pasteFromClipboard() async {
    final clipboardData = await Clipboard.getData(Clipboard.kTextPlain);
    if (clipboardData?.text != null) {
      setState(() {
        _uriController.text = clipboardData!.text!;
      });
    }
  }

  Future<void> _launchApp() async {
    final uri = _uriController.text.trim();
    if (uri.isEmpty) {
      setState(() {
        _errorMessage = '请输入 URI Scheme';
      });
      return;
    }

    try {
      final uriObj = Uri.parse(uri);
      if (await canLaunchUrl(uriObj)) {
        await launchUrl(uriObj);
        setState(() {
          _errorMessage = '';
        });
      } else {
        setState(() {
          _errorMessage = '无法打开此 URI Scheme';
        });
      }
    } catch (e) {
      setState(() {
        _errorMessage = '无效的 URI Scheme';
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('URI Scheme 启动器'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            TextField(
              controller: _uriController,
              decoration: InputDecoration(
                labelText: '输入 URI Scheme',
                hintText: '例如: myapp://open',
                border: const OutlineInputBorder(),
                errorText: _errorMessage.isEmpty ? null : _errorMessage,
              ),
            ),
            const SizedBox(height: 16),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                ElevatedButton.icon(
                  onPressed: _pasteFromClipboard,
                  icon: const Icon(Icons.paste),
                  label: const Text('粘贴'),
                ),
                ElevatedButton.icon(
                  onPressed: _launchApp,
                  icon: const Icon(Icons.launch),
                  label: const Text('拉起'),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  @override
  void dispose() {
    _uriController.dispose();
    super.dispose();
  }
} 