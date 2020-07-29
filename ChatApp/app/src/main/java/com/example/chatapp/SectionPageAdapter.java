package com.example.chatapp;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class SectionPageAdapter extends FragmentPagerAdapter {

    /**
     * Constructor for {@link FragmentPagerAdapter} that sets the fragment manager for the adapter.
     * This is the equivalent of calling  and
     * passing in {@link #BEHAVIOR_SET_USER_VISIBLE_HINT}.
     *
     * <p>Fragments will have {@link Fragment#setUserVisibleHint(boolean)} called whenever the
     * current Fragment changes.</p>
     *
     * @param fm fragment manager that will interact with this adapter
     * @deprecated use  with
     * {@link #BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT}
     */
    public SectionPageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FriendsFrag friendsFrag =  new FriendsFrag();
                return  friendsFrag;

            case 1:
                ChatFrag chatFrag =  new ChatFrag();
                return chatFrag;
            case 2:
                RequestFrag requestFrag = new RequestFrag();
                return requestFrag;

            default:
                return null;
        }

    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return 3;
    }
    public CharSequence getPageTitle(int position){

        switch(position)
        {
            case 0:
                return "FRIENDS";
            case 1:
                return "CHATS";
            case 2:
                return "REQUESTS";
            default:
                return null;
        }
    }
}