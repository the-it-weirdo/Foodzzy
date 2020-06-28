package dev.debaleen.foodrunner.util

import dev.debaleen.foodrunner.model.FAQModel

val faqs = hashMapOf<String, String>(
    Pair("Show an example of placeholder text.", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."),
    Pair("Is multi-paragraph placeholder text possible? If so, give example.", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Sodales ut etiam sit amet nisl purus in mollis. Ut venenatis tellus in metus vulputate eu scelerisque. Pellentesque habitant morbi tristique senectus. Dui id ornare arcu odio ut sem. Ac auctor augue mauris augue neque. Vestibulum rhoncus est pellentesque elit ullamcorper dignissim cras tincidunt. Ut morbi tincidunt augue interdum velit euismod in pellentesque massa. Pharetra sit amet aliquam id diam. Nisl vel pretium lectus quam id leo in. Pharetra sit amet aliquam id diam maecenas ultricies mi. Erat velit scelerisque in dictum non consectetur a erat. Parturient montes nascetur ridiculus mus mauris vitae ultricies leo integer. Sodales ut eu sem integer vitae justo eget magna fermentum. Imperdiet dui accumsan sit amet nulla facilisi morbi tempus iaculis. Scelerisque eu ultrices vitae auctor eu augue ut lectus. Ultrices gravida dictum fusce ut placerat.\n" +
            "\n" +
            "Amet mattis vulputate enim nulla aliquet porttitor lacus luctus. Viverra ipsum nunc aliquet bibendum. Amet consectetur adipiscing elit ut aliquam purus sit amet luctus. Condimentum id venenatis a condimentum. Vitae sapien pellentesque habitant morbi tristique senectus et. Neque ornare aenean euismod elementum nisi quis eleifend. Habitant morbi tristique senectus et netus et malesuada fames. Massa massa ultricies mi quis hendrerit dolor magna eget. Vitae tempus quam pellentesque nec nam. Mi bibendum neque egestas congue quisque egestas diam in. Vulputate enim nulla aliquet porttitor lacus luctus accumsan tortor. Ac tincidunt vitae semper quis lectus. Tristique nulla aliquet enim tortor.\n" +
            "\n" +
            "Mattis rhoncus urna neque viverra. Consectetur lorem donec massa sapien faucibus. Ut sem viverra aliquet eget sit amet tellus. A iaculis at erat pellentesque adipiscing. Eu tincidunt tortor aliquam nulla facilisi. Facilisi morbi tempus iaculis urna. Rhoncus mattis rhoncus urna neque viverra. Vitae et leo duis ut diam. Praesent elementum facilisis leo vel fringilla est ullamcorper. Cursus turpis massa tincidunt dui ut ornare. Malesuada proin libero nunc consequat interdum varius sit amet mattis. Nisl condimentum id venenatis a condimentum. Enim facilisis gravida neque convallis a. Lectus mauris ultrices eros in. Sed risus ultricies tristique nulla aliquet enim tortor. Dictumst quisque sagittis purus sit amet. Ornare massa eget egestas purus. Rutrum quisque non tellus orci ac auctor augue mauris. Eu lobortis elementum nibh tellus molestie nunc non.\n" +
            "\n" +
            "Amet nisl suscipit adipiscing bibendum est ultricies integer quis auctor. Commodo viverra maecenas accumsan lacus vel facilisis volutpat est velit. Laoreet id donec ultrices tincidunt arcu non. Feugiat in fermentum posuere urna nec. Pharetra convallis posuere morbi leo urna molestie at. Nibh sit amet commodo nulla facilisi nullam vehicula. Arcu cursus vitae congue mauris rhoncus aenean. Lorem ipsum dolor sit amet. Varius sit amet mattis vulputate enim nulla aliquet porttitor lacus. Sapien eget mi proin sed libero. Quisque egestas diam in arcu cursus euismod. Enim nec dui nunc mattis enim ut. Integer quis auctor elit sed vulputate mi sit amet mauris. Quis commodo odio aenean sed adipiscing diam.\n" +
            "\n" +
            "Est ultricies integer quis auctor elit sed. Malesuada fames ac turpis egestas sed tempus urna et. Nam aliquam sem et tortor consequat id porta. Viverra nibh cras pulvinar mattis. Commodo viverra maecenas accumsan lacus vel facilisis volutpat est velit. Cursus metus aliquam eleifend mi in nulla posuere sollicitudin aliquam. Tellus pellentesque eu tincidunt tortor aliquam. Facilisi morbi tempus iaculis urna id. Enim ut tellus elementum sagittis. Nisi quis eleifend quam adipiscing vitae proin sagittis nisl rhoncus. Varius sit amet mattis vulputate enim. Id diam maecenas ultricies mi eget mauris pharetra et. Faucibus vitae aliquet nec ullamcorper. In nulla posuere sollicitudin aliquam ultrices sagittis orci a. Blandit libero volutpat sed cras ornare arcu dui. Dolor sed viverra ipsum nunc aliquet bibendum enim facilisis. Consectetur adipiscing elit ut aliquam purus. Egestas congue quisque egestas diam in arcu. Libero nunc consequat interdum varius sit amet.")
)

fun formFaqs(faqs: HashMap<String, String>): ArrayList<FAQModel> {
    val returnList = arrayListOf<FAQModel>()

    faqs.map {
        returnList.add(
            FAQModel(
                question = it.key,
                answer = it.value
            )
        )
    }

    return returnList
}