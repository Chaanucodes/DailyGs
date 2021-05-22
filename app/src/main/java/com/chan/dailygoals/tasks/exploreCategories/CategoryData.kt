package com.chan.dailygoals.tasks.exploreCategories

object CategoryData {
    private val expandableListDetail : HashMap<String, List<String>> = hashMapOf()

    fun getData() : HashMap<String, List<String>> {

        val health  = ArrayList<String>();
        health.add("Walking");
        health.add("Running");
        health.add("Workout");
        health.add("Yoga");
        health.add("Stretching");
        health.add("Taking medicine");

        val general = ArrayList<String>();
        general.add("Studying");
        general.add("Charging phone");
        general.add("Practicing new language");
        general.add("Reading");
        general.add("Shopping");

        val miscellaneous = ArrayList<String>()
        miscellaneous.add("Dancing")
        miscellaneous.add("Planning expenses")
        miscellaneous.add("Creating video content")
        miscellaneous.add("Calling friends")
        miscellaneous.add("Making an app")
        miscellaneous.add("Drawing shapes")
        miscellaneous.add("Replying to messages")
        miscellaneous.add("Getting fresh air")


        expandableListDetail["Health"] = health;
        expandableListDetail["General"] = general;
        expandableListDetail["Miscellaneous"] = miscellaneous;
        return expandableListDetail;
    }

    fun addData(myCategories : ArrayList<String>){
        expandableListDetail["My Categories"] = myCategories
    }
}