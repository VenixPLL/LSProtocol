[![Discord](https://img.shields.io/discord/1255209755757248643)](https://discord.gg/rAhJY2Sh2y)

# LSProtocol

LSProtocol is a free and open-source Minecraft protocol library that supports **ViaVersion/ViaBackwards/ViaRewind**

### Build Requirements
- Java 21

### Features
- Ability to create your own packets (for adding support for base 1.8.x(47) protocol)
- Enabling support for entire ViaVersion using [ViaLoader](https://github.com/ViaVersion/ViaLoader)
- Base support for the 1.20.4 protocol **(Some packets are missing)**
- Read unimplemented packets as RawPacket
- Readable and easy-to use examples.

### Documentation
- Check out the examples/tests provided [here](/src/test/java/legacyTest)
- Official documentation is available [here](/doc/USAGE.md)
- Check out [wiki.vg](https://wiki.vg/index.php?title=Protocol&oldid=19208) for implemented base version 1.20.4

### Issues
If you encounter any bugs or identify missing features, please inform us by opening an issue.

### Contributing
We welcome and appreciate contributions from the community. If you wish to support our project, you are encouraged to make changes to the LSProtocol source code and submit a pull request. Your contributions help us improve and grow the project.

### Optimizations
This project is using VarInt and buffer optimizations used in [Velocity](https://github.com/PaperMC/Velocity)/[Paper](https://github.com/PaperMC/Paper)/[Krypton](https://github.com/astei/krypton)
Made by **[Andrew Steinborn](https://github.com/astei)**  
``https://steinborn.me/posts/performance/how-fast-can-you-write-a-varint/``

Also, this project is using a very fast packet registry system built on EnumMap.  
**Note: This packet registry might break in future protocol updates due to packetId being read as a byte instead of VarInt/Short**

# Need Help?
We're here to assist you! If you have any questions about using the library, encounter issues, or have suggestions for improvement, don't hesitate to reach out.

### Here are some ways we can help:
- **Questions:** Feel free to open an issue and ask how to achieve something specific with the library.
- **Problems:** If you encounter any difficulties, please open an issue to describe the problem and we'll be happy to assist.
- **Improvements:** We value your feedback! If you have suggestions on how to make the documentation clearer or more helpful, please open an issue and share your thoughts.

### FAQ
- No questions asked so far.

### Used libraries
- [Netty 4.1.107](https://github.com/netty/netty) (not all components are used)
- [Kyori Adventure Serializer/NBT](https://github.com/KyoriPowered/adventure)
- [Steveice10 OpenNBT](https://github.com/GeyserMC/OpenNBT) transferred to [ViaNBT](https://github.com/ViaVersion/ViaNBT)
- [Velocity Native Cipher/Compression](https://github.com/PaperMC/Velocity/tree/dev/3.0.0/native)
- [RaphiMC ViaLoader](https://github.com/ViaVersion/ViaLoader)
- [ViaVersion](https://github.com/ViaVersion)/[ViaBackwards](https://github.com/ViaVersion/ViaBackwards)/[ViaRewind](https://github.com/ViaVersion/ViaRewind)
- [EventAPI](https://bitbucket.org/DarkMagician6/eventapi) (unused right now)

### License
LSProtocol is licensed under the GPLv3 license.
### Additional Clause on Third-Party Code

This project incorporates code from various third-party libraries and contributors. In recognition of their contributions, we include the following provision:

Third-Party Code Rights:

- **Right to Request Removal:** The original author(s) of any code used in this project may request the removal of their code by contacting the project maintainers. Upon receiving such a request, the maintainers will make reasonable efforts to remove the specified code in a timely manner.

- **Right to Request Improved Attribution:** The original author(s) may request enhanced attribution for their code, including more detailed description or documentation, by contacting the project maintainers. The maintainers will consider such requests and make reasonable efforts to comply.

- **Scope and Limitations:** These rights apply solely to the portions of the code that were authored by the requesting party and incorporated into this project. The maintainers reserve the right to reject requests that cannot be reasonably accommodated or that would significantly disrupt the functionality of the project.