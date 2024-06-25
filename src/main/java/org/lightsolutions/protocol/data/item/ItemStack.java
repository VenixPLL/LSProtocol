package org.lightsolutions.protocol.data.item;

import com.github.steveice10.opennbt.tag.builtin.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class ItemStack {

    private final int itemId;
    private int count;
    private Tag itemTag;

}
