import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  static const _platform = MethodChannel('crypto.labsec.dev/info');
  final TextEditingController _messageController = TextEditingController();
  String _message = '>> No verification was done yet';

  @override
  void dispose() {
    _messageController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: GestureDetector(
        onTap: () => FocusManager.instance.primaryFocus?.unfocus(),
        child: Scaffold(
          resizeToAvoidBottomInset: false,
          appBar: AppBar(
            title: Text('Verify Ed448 Signature'),
            centerTitle: true,
          ),
          body: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 64.0),
                  child: TextField(
                    controller: _messageController,
                    decoration: InputDecoration(
                      hintText: "Message to be verified",
                    ),
                  ),
                ),
                ElevatedButton(
                  onPressed: () {
                    _verifyMessage();
                    if (_messageController.text.isNotEmpty) {
                      FocusManager.instance.primaryFocus?.unfocus();
                    }
                    _messageController.clear();
                  },
                  child: Text('Verify'),
                ),
                Container(
                  width: 300.0,
                  height: 300.0,
                  padding: EdgeInsets.all(16.0),
                  color: Colors.black54,
                  child: ListView(
                    //crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Correct message: \"1234567890\"',
                        style: TextStyle(
                          color: Colors.white70,
                          fontWeight: FontWeight.bold,
                          fontSize: 16.0,
                        ),
                      ),
                      SizedBox(
                        height: 30.0,
                      ),
                      Text(
                        "Verification results:",
                        style: TextStyle(
                          color: Colors.white70,
                          fontWeight: FontWeight.bold,
                          fontSize: 16.0,
                        ),
                      ),
                      Text(
                        _message,
                        style: TextStyle(
                          color: Colors.lightBlueAccent[100],
                          fontWeight: FontWeight.bold,
                          fontSize: 16.0,
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Future<void> _verifyMessage() async {
    Map argumentsMap = <String, dynamic>{'message': _messageController.text};
    String result;
    if (_messageController.text.length != 0) {
      try {
        final String receivedMessage =
            await _platform.invokeMethod('verifyMessage', argumentsMap);
        result = receivedMessage;
      } on PlatformException catch (e) {
        result = 'Failed to verify the message: ${e.message}';
      }

      setState(() {
        _message = result;
      });
    }
  }
}
